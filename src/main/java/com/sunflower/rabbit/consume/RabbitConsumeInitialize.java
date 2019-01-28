package com.sunflower.rabbit.consume;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.sunflower.rabbit.common.annotation.RabbitMqHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xzhen
 * @created 15:52 27/01/2019
 * @description TODO
 */
@Slf4j
@Configuration
public class RabbitConsumeInitialize implements InitializingBean {

    @Value("${spring.application.name:}")
    private String appName;

    @Autowired
    private RabbitConsumeProp rabbitConsumeProp;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ConsumerListenerHandler consumerListenerHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (rabbitConsumeProp != null) {
            declareRabbitBind(convert(rabbitConsumeProp.getConsumer()));
        }
    }

    private void declareRabbitBind(List<RabbitBind> rabbitBinds) throws IOException {
        if (CollectionUtils.isEmpty(rabbitBinds)) {
            return;
        }
        Map<String, Exchange> exchangeBeans = applicationContext.getBeansOfType(Exchange.class);
        Map<String, Queue> queueBeans = applicationContext.getBeansOfType(Queue.class);
        Map<String, Consumer> consumerBeans = applicationContext.getBeansOfType(Consumer.class);

        List<String> queueList = new ArrayList<>();
        List<String> exchangeList = new ArrayList<>();
        for (RabbitBind rabbitBind : rabbitBinds) {
            String queue = rabbitBind.getQueue();
            String exchange = rabbitBind.getExchange();
            if (!checkConsumerHandle(consumerBeans, rabbitBind.getHandleName())) {
                log.warn("the rabbitMQ key: {} missing listener handler", rabbitBind.getHandleName());
                continue;
            }
            boolean queueEmpty = StringUtils.isEmpty(queue);

            if (!queueEmpty && checkQueue(queueBeans, queueList, queue)) {
                queueList.add(queue);
            }
            if (checkExchange(exchangeBeans, exchangeList, exchange)) {
                exchangeList.add(exchange);
            }
        }
        if (queueList.isEmpty() && exchangeList.isEmpty()) {
            return;
        }
        Channel channel = connectionFactory.createConnection().createChannel(false);
        if (!queueList.isEmpty()) {
            declareQueue(queueList, channel);
        }
        if (!exchangeList.isEmpty()) {
            declareExchange(exchangeList, channel);
        }
        declareBind(rabbitBinds, channel);
        consumerListenerHandler.listenerHandle(consumerBeans.values(), rabbitBinds);
    }

    private void declareQueue(List<String> queueBeans, Channel channel) throws IOException {
        for (String queue : queueBeans) {
            channel.queueDeclare(queue, true, false, false, null);
        }
    }

    private void declareExchange(List<String> exchangeBeans, Channel channel) throws IOException {
        for (String exchange : exchangeBeans) {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true);
        }
    }

    private void declareBind(List<RabbitBind> rabbitBindList, Channel channel) throws IOException {
        for (RabbitBind rabbitBind : rabbitBindList) {
            String[] routeKeys = rabbitBind.getRouteKey();
            String queue = rabbitBind.getQueue();
            if (StringUtils.isEmpty(queue)) {
                queue = channel.queueDeclare().getQueue();
                rabbitBind.setQueue(queue);
            }
            if (routeKeys == null || routeKeys.length == 0) {
                channel.queueBind(queue, rabbitBind.getExchange(), RabbitBind.DEFAULT_ROUTE);
            } else {
                for (String routeKey : routeKeys) {
                    channel.queueBind(queue, rabbitBind.getExchange(), routeKey.trim());
                }
            }
        }
    }

    private boolean checkQueue(Map<String, Queue> queueBeans, List<String> queueList, String queue) {
        if (queueList.contains(queue)) {
            return false;
        }
        for (Queue q : queueBeans.values()) {
            if (q.getName().equals(queue)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkExchange(Map<String, Exchange> exchangeBeans, List<String> exchangeList, String exchange) {
        if (exchangeList.contains(exchange)) {
            return false;
        }
        for (Exchange value : exchangeBeans.values()) {
            if (value.getName().equals(exchange)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkConsumerHandle(Map<String, Consumer> consumerBeans, String key) {
        if (consumerBeans.isEmpty()) {
            return false;
        }
        for (Consumer consumer : consumerBeans.values()) {
            Class<? extends Consumer> cls = consumer.getClass();
            RabbitMqHandler annotation = cls.getAnnotation(RabbitMqHandler.class);
            if (annotation == null) {
                throw new RuntimeException("class: [" + cls.getName() + "] missing annotation: [" + RabbitMqHandler.class.getName() + "]");
            }
            String value = annotation.value();
            if (key.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private List<RabbitBind> convert(Map<String, Map<String, String>> paramMap) {
        if (CollectionUtils.isEmpty(paramMap)) {
            log.info("没有配置 exchange");
            return null;
        }

        List<RabbitBind> rabbitBindList = new ArrayList<>();

        for (Map.Entry<String, Map<String, String>> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            Map<String, String> value = entry.getValue();

            RabbitBind rabbitBind = new RabbitBind();
            rabbitBind.setHandleName(key);

            String queue = value.get(RabbitBind.QUEUE);
            String routeKey = value.get(RabbitBind.ROUTE_KEY);
            String exchange = value.get(RabbitBind.EXCHANGE);
            rabbitBind.setQueue(queue);
            rabbitBind.setExchange(StringUtils.isEmpty(exchange) ? appName : exchange);
            if (!StringUtils.isEmpty(routeKey)) {
                String[] keys = StringUtils.commaDelimitedListToStringArray(routeKey);
                rabbitBind.setRouteKey(keys);
            }
            rabbitBindList.add(rabbitBind);
        }
        return rabbitBindList;
    }
}
