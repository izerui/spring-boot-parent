package com.yj2025.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private MessageProducer messageProducer;

    @GetMapping("/kafka/test")
    public String kafkaTest(){
        messageProducer.send("platform.kafka.error.test", "manager-pc", Map.of("productId", "MANUFACTURE","entCode","415318434"), Map.of("productIddd", "2"));
        return "测试";
    }

    @KafkaListener(topics = {"platform.kafka.error.test"})
    public void kafkaTestd(ConsumerRecord<Object,Object> map){
        log.info("收到消息");
        throw new RuntimeException("测试");
    }
}
