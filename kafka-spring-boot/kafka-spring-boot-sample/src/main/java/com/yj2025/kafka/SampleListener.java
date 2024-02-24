package com.yj2025.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j(topic = "[接收端]:")
@Component
public class SampleListener {

    @KafkaListener(topics = "testTopic")
    @Transactional
    public void listenerTestTopic(Map<String, Object> map) {
        log.info("接收到消息: {}", map);
    }
}
