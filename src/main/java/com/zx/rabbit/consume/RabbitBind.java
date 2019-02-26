package com.zx.rabbit.consume;

import lombok.Data;

/**
 * @author xzhen
 * @created 20:40 27/01/2019
 * @description TODO
 */
@Data
public class RabbitBind {
    public static final String QUEUE = "queue";
    public static final String ROUTE_KEY = "route-key";
    public static final String EXCHANGE = "exchange";
    public static final String DEFAULT_ROUTE = "";

    private String handleName;
    private String queue;
    private String[] routeKey;
    private String exchange;
}
