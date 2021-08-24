package com.yj2025.websocket;

import lombok.Data;

@Data
public class RabbitWebSocketProperties {

    private String exchange = "ierp";
    private String routingKey = "websocket";
}
