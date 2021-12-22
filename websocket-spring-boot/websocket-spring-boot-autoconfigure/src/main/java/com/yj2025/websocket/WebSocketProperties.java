package com.yj2025.websocket;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperties {

    private String type = "rabbit";

    @NestedConfigurationProperty
    private RabbitWebSocketProperties rabbit;
}
