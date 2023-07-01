package com.yj2025.websocket.server.impl.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(name = "websocket.server.listener-type", havingValue = "kafka")
public class KafkaConfiguration {


    @PostConstruct
    public void init() {
        throw new UnsupportedOperationException("暂未实现");
    }
}
