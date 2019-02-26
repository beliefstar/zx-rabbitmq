package com.zx.rabbit.test;

import com.zx.rabbit.consume.Consumer;

/**
 * @author xzhen
 * @created 15:55 28/01/2019
 * @description TODO
 */
//@RabbitMQHandler("logs")
public class LogsHandler implements Consumer<String> {

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
