package com.yj2025.audit.sample.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Slf4j
public class AuditListener {

    @RabbitListener(queues = "test.audit")
    public void audit(String message) {
        log.info(message);
    }
}
