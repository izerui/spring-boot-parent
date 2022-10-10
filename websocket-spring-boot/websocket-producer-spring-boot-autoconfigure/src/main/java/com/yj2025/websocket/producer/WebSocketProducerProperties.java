package com.yj2025.websocket.producer;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "websocket.producer")
public class WebSocketProducerProperties {

    private ProducerType type = ProducerType.RABBIT;

    @NestedConfigurationProperty
    private RabbitWebSocketProperties rabbit = new RabbitWebSocketProperties();

    @Data
    public static class RabbitWebSocketProperties {

        private String exchange;
        private String routingKey;
    }

    public enum ProducerType {
        RABBIT,
        // 暂未实现
        KAFKA;
    }
}
