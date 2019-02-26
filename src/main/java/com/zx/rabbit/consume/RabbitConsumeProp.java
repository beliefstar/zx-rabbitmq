package com.zx.rabbit.consume;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author xzhen
 * @created 15:22 27/01/2019
 * @description TODO
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq.binding")
public class RabbitConsumeProp {

    private Map<String, Map<String, String>> consumer;


}
