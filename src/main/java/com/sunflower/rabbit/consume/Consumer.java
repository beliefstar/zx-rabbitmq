package com.sunflower.rabbit.consume;

/**
 * @author xzhen
 * @created 14:58 27/01/2019
 * @description TODO
 */
public interface Consumer<T> {

    boolean handle(T object);

    void handleRetryMax(T object);
}
