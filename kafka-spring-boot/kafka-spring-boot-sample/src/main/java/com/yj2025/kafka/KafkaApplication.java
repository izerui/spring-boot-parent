package com.yj2025.kafka;

import com.yj2025.basic.support.Context;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "[发送端]:")
@SpringBootApplication
public class KafkaApplication implements CommandLineRunner {

    @Autowired
    private MessageProducer messageProducer;

    @Override
    public void run(String... args) throws Exception {
        Context.runDelayed("循环发送消息",
                integer -> {
                    send(integer + 1);
                    return true;
                },
                3,
                10,
                100,
                true
        );
    }

    private void send(Integer num) {
        for (int i = num; i < num + 10; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            map.put("name", "liuyuhua" + i);
            map.put("age", 30 + i);
            messageProducer.send("testTopic", "key001", map, Map.of("property001", "value001"), new ProducerCallback() {
                @Override
                public void onSuccess(SendResult<String, Object> result) {
                    log.info("发送成功: {}", result.toString());
                }

                @Override
                public void onFailure(Throwable throwable) {
                    log.error("发送失败: ", throwable);
                }
            });
        }

    }


    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }
}
