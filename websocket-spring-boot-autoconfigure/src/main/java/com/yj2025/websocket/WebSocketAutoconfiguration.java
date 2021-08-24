package com.yj2025.websocket;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketAutoconfiguration {


    @Bean
    @ConditionalOnProperty(name = "websocket.type", matchIfMissing = true, havingValue = "rabbit")
    public WebSocketContext webSocketContext(RabbitTemplate rabbitTemplate, WebSocketProperties webSocketProperties) {
        return new RabbitWebSocketContextImpl(rabbitTemplate, webSocketProperties.getRabbit());
    }
}
