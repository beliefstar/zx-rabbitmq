package com.sunflower.rabbit.test;

import com.sunflower.rabbit.common.annotation.RabbitMqHandler;
import com.sunflower.rabbit.consume.Consumer;

/**
 * @author xzhen
 * @created 15:54 28/01/2019
 * @description TODO
 */
@RabbitMqHandler("sms")
public class TestHandle implements Consumer<String> {
    @Override
    public boolean handle(String object) {
        System.out.println(object);
        return true;
    }

    @Override
    public void handleRetryMax(String object) {
        System.out.println(object);
    }
}
