package com.sunflower.rabbit.produce;

import com.sunflower.rabbit.common.model.Message;
import com.sunflower.rabbit.common.serialize.ObjectSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author xzhen
 * @created 15:33 27/01/2019
 * @description TODO
 */
@Slf4j
@Component
public class RabbitProducer implements Producer, ConfirmCallback {

    @Autowired
    private ObjectSerializer objectSerializer;

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setConfirmCallback(this);
    }

    @Override
    public void send(Message message) {
        Assert.notNull(message, "message");
        Assert.notNull(message.getExchange(), "exchange");
        Assert.notNull(message.getRouteKey(), "routeKey");

        byte[] serialize = objectSerializer.serialize(message);
        rabbitTemplate.convertAndSend(message.getExchange(), message.getRouteKey(), serialize);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("send message success!");
        } else {
            log.info("send message fail! cause: {}", cause);
        }
    }
}
