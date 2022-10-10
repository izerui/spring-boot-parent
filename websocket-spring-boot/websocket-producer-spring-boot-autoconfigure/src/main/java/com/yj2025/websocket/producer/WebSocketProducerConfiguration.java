package com.yj2025.websocket.producer;

import com.yj2025.websocket.producer.impl.RabbitContextImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WebSocketProducerProperties.class)
public class WebSocketProducerConfiguration {


    @Bean
    @ConditionalOnProperty(name = "websocket.producer.type", matchIfMissing = true, havingValue = "rabbit")
    public WebSocketContext webSocketContext(RabbitTemplate rabbitTemplate, WebSocketProducerProperties webSocketProperties) {
        return new RabbitContextImpl(rabbitTemplate, webSocketProperties);
    }

}
