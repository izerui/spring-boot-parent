package com.yj2025.rabbit.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class RabbitSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Async
    public void send() {
        Map map = new HashMap();
        map.put("type", "测试");
        rabbitTemplate.convertAndSend("test", "test.queue001", map);
        log.info("发送成功, tx: {}", TransactionSynchronizationManager.isActualTransactionActive());
    }
}
