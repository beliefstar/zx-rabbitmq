package com.zx.rabbit.produce;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xzhen
 * @created 19:55 27/01/2019
 * @description TODO
 */
@Configuration
public class RabbitProduceInitialize implements InitializingBean {

    @Autowired
    private RabbitProduceProp rabbitProduceProp;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!CollectionUtils.isEmpty(rabbitProduceProp.getExchange())) {
            declareExchange(rabbitProduceProp.getExchange());
        }
        if (!CollectionUtils.isEmpty(rabbitProduceProp.getQueue())) {
            declareQueue(rabbitProduceProp.getQueue());
        }
    }

    private void declareExchange(List<String> exchangeList) throws IOException {
        Map<String, Exchange> exchangeBeans = applicationContext.getBeansOfType(Exchange.class);
        exchangeList = exchangeList.stream().filter(exchange -> {
            if (StringUtils.isEmpty(exchange)) {
                return false;
            }
            for (Exchange value : exchangeBeans.values()) {
                if (value.getName().equals(exchange)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(exchangeList)) {
            return;
        }
        Channel channel = connectionFactory.createConnection().createChannel(false);
        for (String exchange : exchangeList) {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true);
        }
    }

    private void declareQueue(List<String> queueList) throws IOException {
        Map<String, Queue> queueBeans = applicationContext.getBeansOfType(Queue.class);
        queueList = queueList.stream().filter(queue -> {
            if (StringUtils.isEmpty(queue)) {
                return false;
            }
            for (Queue value : queueBeans.values()) {
                if (value.getName().equals(queue)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(queueList)) {
            return;
        }
        Channel channel = connectionFactory.createConnection().createChannel(false);
        for (String queue : queueList) {
            channel.queueDeclare(queue, true, false, false, null);
        }
    }
}
