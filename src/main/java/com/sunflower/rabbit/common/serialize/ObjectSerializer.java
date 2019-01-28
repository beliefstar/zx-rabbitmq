package com.sunflower.rabbit.common.serialize;

/**
 * @author xzhen
 * @created 16:32 27/01/2019
 * @description TODO
 */
public interface ObjectSerializer {

    byte[] serialize(Object object);

    Object deserialize(byte[] body);
}
