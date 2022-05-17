package com.yj2025.websocket.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "websocket.server")
public class WebSocketServerProperties {

    private Integer port = 8066;
    private ChannelCache cache = new ChannelCache();
    private MessageListenerType listenerType = MessageListenerType.RABBIT;
    private Rabbit rabbit = new Rabbit();

    @Data
    public static class ChannelCache {
        private String userIdPrefix = "ws_uid:";
        private Integer channelIdTimeoutMinutes = 5;

    }

    public enum MessageListenerType {
        RABBIT,
        KAFKA;
    }

    @Data
    public class Rabbit {
        private String exchange = "ierp";
        private String routingKey = "websocket";
    }

}
