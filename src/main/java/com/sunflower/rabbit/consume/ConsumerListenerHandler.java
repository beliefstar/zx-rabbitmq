package com.sunflower.rabbit.consume;

import com.sunflower.rabbit.common.annotation.RabbitMqHandler;
import com.sunflower.rabbit.common.model.Message;
import com.sunflower.rabbit.common.serialize.ObjectSerializer;
import com.sunflower.rabbit.produce.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author xzhen
 * @created 20:37 27/01/2019
 * @description TODO
 */
@Slf4j
@Component
public class ConsumerListenerHandler {

    @Value("${spring.rabbitmq.binding.default-max-retry-count:5}")
    private int maxRetryCount;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectSerializer objectSerializer;

    @Autowired
    private Producer producer;

    private static final String BEAN_POST = "_listener";

    public void listenerHandle(Collection<Consumer> consumerList, List<RabbitBind> rabbitBinds) {
        for (RabbitBind rabbitBind : rabbitBinds) {
            SimpleMessageListenerContainer bean = null;
            try {
                bean = applicationContext.getBean(rabbitBind.getHandleName() + BEAN_POST, SimpleMessageListenerContainer.class);
            } catch (BeansException e) {
            }
            if (bean != null) {
                bean.addQueueNames(rabbitBind.getQueue());
                continue;
            }
            bean = new SimpleMessageListenerContainer();
            bean.setConnectionFactory(connectionFactory);
            bean.setQueueNames(rabbitBind.getQueue());
//            bean.setMessageConverter(new KryoMessageConvert());
            bean.setAcknowledgeMode(AcknowledgeMode.MANUAL);  // autoAck = false
            Consumer consumer = getConsumer(consumerList, rabbitBind.getHandleName());
            bean.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
                byte[] body = message.getBody();
                boolean retry = false;
                Message m = null;
                try {
                    m = (Message) objectSerializer.deserialize(body);
                } catch (Exception e) {
                    log.info("object serialize fail! msg: {}", e.getMessage());
                    retry = true;
                } finally {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                }

                if (!retry) {
                    boolean success = false;
                    try {
                        success = consumer.handle(m.getData());
                    } catch (Exception e) {
                        log.info("execute handle fail! msg: {}", e.getMessage());
                    }
                    retry = !success;
                }
                if (retry) {
                    retry(consumer, m);
                }
            });
            registerBean(rabbitBind.getHandleName(), bean);
        }
    }

    private void retry(Consumer consumer, Message message) {
        if (message == null) {
            log.error("message is null!");
            return;
        }
        int retryCount = message.getRetryCount();
        if (retryCount >= maxRetryCount) {
            log.info("message retry count is max count: {}", retryCount);
            consumer.handleRetryMax(message.getData());
        } else {
            log.info("message retry! count is {}", retryCount);
            retryCount++;
            message.setRetryCount(retryCount);
            producer.send(message);
        }
    }

    private void registerBean(String key, Object object) {
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        factory.registerSingleton(key + BEAN_POST, object);
    }

    private Consumer getConsumer(Collection<Consumer> consumers, String name) {
        for (Consumer consumer : consumers) {
            Class<? extends Consumer> cls = consumer.getClass();
            RabbitMqHandler annotation = cls.getAnnotation(RabbitMqHandler.class);
            String value = annotation.value();
            if (name.equals(value)) {
                return consumer;
            }
        }
        throw new RuntimeException("the key [" + name + "] missing consumer");
    }

}
