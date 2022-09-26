package com.yj2025.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class AbstractMessageSender implements IMessageRouter {

    private RabbitTemplate rabbitTemplate;

    public AbstractMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public final void sendMessage(Object msg) {
        rabbitTemplate.convertAndSend(getExchange(), getRoutingKey(), msg);
    }
}
