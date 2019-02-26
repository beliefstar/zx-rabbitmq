package com.zx.rabbit.produce;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author xzhen
 * @created 15:22 27/01/2019
 * @description TODO
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq.binding.producer")
public class RabbitProduceProp {

    private List<String> exchange;

    private List<String> queue;

}
