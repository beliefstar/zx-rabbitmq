package com.zx.rabbit.common.model;

import lombok.Data;

/**
 * @author xzhen
 * @created 15:00 27/01/2019
 * @description TODO
 */
@Data
public class Message {

    private String exchange;

    private String routeKey;

    private int retryCount = 0;

    private Object data;
}
