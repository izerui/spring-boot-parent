package com.yj2025.websocket.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "websocket.server")
public class WebSocketServerProperties {

    private Integer port = 8066;
    /**
     * 用户Id缓存的key前缀
     */
    private String userIdPrefix = "ws_uid:";
    /**
     * 保存在redis中的临时websocket channelId的有效时长 单位：分钟
     */
    private Integer channelIdTimeoutMinutes = 5;
    /**
     * 消息监听的实现类型，kafka暂未实现，现只支持rabbit模式
     */
    private MessageListenerType listenerType = MessageListenerType.RABBIT;
    private Rabbit rabbit = new Rabbit();

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
