package com.yj2025.rabbit.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMessageListener {

    @RabbitListener(queues = "test.queue001")
    public void listener(String message) {
        log.info(message);
    }

}
