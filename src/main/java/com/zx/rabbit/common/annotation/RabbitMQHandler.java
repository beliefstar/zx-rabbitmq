package com.zx.rabbit.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author xzhen
 * @created 20:09 27/01/2019
 * @description TODO
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RabbitMQHandler {
    String value();
}
