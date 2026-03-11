package com.yj2025.rabbit;

public interface IMessageRouter {

    String getExchange();

    String getRoutingKey();
}
