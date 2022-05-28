package com.yj2025.websocket.server.impl.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.websocket.server.impl.UserChannelService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "websocket.server.listener-type", matchIfMissing = true, havingValue = "rabbit")
public class RabbitConfiguration {

    @Bean
    public RabbitQueueName queueName() {
        return new RabbitQueueName();
    }

    @Bean
    public RabbitMessageListener rabbitMessageListener(ObjectMapper objectMapper, UserChannelService userChannelService) {
        return new RabbitMessageListener(objectMapper, userChannelService);
    }


}
