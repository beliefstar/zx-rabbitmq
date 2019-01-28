package com.sunflower.rabbit.produce;


import com.sunflower.rabbit.common.model.Message;

/**
 * @author xzhen
 * @created 14:57 27/01/2019
 * @description 生产者
 */
public interface Producer {

    void send(Message message);
}
