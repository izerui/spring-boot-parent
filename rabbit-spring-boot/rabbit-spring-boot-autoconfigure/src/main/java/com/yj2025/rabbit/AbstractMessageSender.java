package com.yj2025.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class AbstractMessageSender implements IMessageRouter {

    private RabbitTemplate rabbitTemplate;

    public AbstractMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 业务中无特殊情况不允许自定义消息格式
     *
     * @param msg
     */
    public final void sendMessage(Object msg) {
        rabbitTemplate.convertAndSend(getExchange(), getRoutingKey(), msg);
    }

    public final void sendMessage(SourceMessageVO messageVO) {
        rabbitTemplate.convertAndSend(getExchange(), getRoutingKey(), messageVO);
    }
}
