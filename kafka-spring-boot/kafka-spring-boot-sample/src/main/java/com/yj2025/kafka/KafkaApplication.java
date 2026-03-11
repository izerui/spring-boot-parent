package com.yj2025.kafka;

import com.yj2025.basic.support.Context;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j(topic = "[发送端]:")
@SpringBootApplication
public class KafkaApplication implements CommandLineRunner {

    @Autowired
    private SampleSender sampleSender;

    @Override
    public void run(String... args) throws Exception {
        Context.runDelayed("循环发送消息",
                integer -> {
                    sampleSender.send(integer + 1);
                    return true;
                },
                3,
                10,
                100,
                true
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }
}
