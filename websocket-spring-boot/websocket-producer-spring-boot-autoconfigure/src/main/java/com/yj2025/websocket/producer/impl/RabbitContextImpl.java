package com.yj2025.websocket.producer.impl;

import com.yj2025.websocket.WebMsg;
import com.yj2025.websocket.producer.WebSocketContext;
import com.yj2025.websocket.producer.WebSocketProducerProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitContextImpl implements WebSocketContext {

    private RabbitTemplate rabbitTemplate;
    private WebSocketProducerProperties.RabbitWebSocketProperties rabbitWebSocketProperties;

    public RabbitContextImpl(RabbitTemplate rabbitTemplate, WebSocketProducerProperties.RabbitWebSocketProperties rabbitWebSocketProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitWebSocketProperties = rabbitWebSocketProperties;
    }

    @Override
    public void sendMessage(WebMsg webMsg) {
        rabbitTemplate.convertAndSend(rabbitWebSocketProperties.getExchange(), rabbitWebSocketProperties.getRoutingKey(), webMsg);
    }
}
