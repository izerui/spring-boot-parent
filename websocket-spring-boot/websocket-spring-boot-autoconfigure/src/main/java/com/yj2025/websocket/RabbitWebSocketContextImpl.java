package com.yj2025.websocket;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

class RabbitWebSocketContextImpl implements WebSocketContext {

    private RabbitTemplate rabbitTemplate;
    private RabbitWebSocketProperties rabbitWebSocketProperties;

    public RabbitWebSocketContextImpl(RabbitTemplate rabbitTemplate, RabbitWebSocketProperties rabbitWebSocketProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitWebSocketProperties = rabbitWebSocketProperties;
    }

    @Override
    public void sendMessage(WebMsg webMsg) {
        rabbitTemplate.convertAndSend(rabbitWebSocketProperties.getExchange(), rabbitWebSocketProperties.getRoutingKey(), webMsg);
    }
}
