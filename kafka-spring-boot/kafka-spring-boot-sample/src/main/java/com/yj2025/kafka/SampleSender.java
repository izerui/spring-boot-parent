package com.yj2025.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "[发送端]:")
@Service
public class SampleSender {

    @Autowired
    private MessageProducer messageProducer;

    @Transactional
    public void send(Integer num) {
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
        throw new RuntimeException("故意抛出异常，让事务回滚，消息不发出");
    }

}
