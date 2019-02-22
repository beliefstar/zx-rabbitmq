package com.sunflower.rabbit.common.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xzhen
 * @created 15:04 27/01/2019
 * @description TODO
 */
@ComponentScan({"com.sunflower.rabbit"})
@Configuration
public class RabbitConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory factory) {
        factory.setPublisherConfirms(true);
        RabbitTemplate template = new RabbitTemplate(factory);
//        template.setMessageConverter(new KryoMessageConvert());
        return template;
    }

}
