package com.yj2025.audit.sample.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Transactional
@Slf4j
public class AuditListener {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public void clear() {
        atomicInteger.set(0);
    }

    @RabbitListener(queues = "test.audit")
    public void audit(String message) {
        log.info("{}", atomicInteger.getAndIncrement());
    }
}
