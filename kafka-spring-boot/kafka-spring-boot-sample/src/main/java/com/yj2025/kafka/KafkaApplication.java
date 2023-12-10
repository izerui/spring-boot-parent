package com.yj2025.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class KafkaApplication implements CommandLineRunner {

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("name", "liuyuhua");
        map.put("age", 30);
        kafkaTemplate.send("testTopic", map);
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }
}
