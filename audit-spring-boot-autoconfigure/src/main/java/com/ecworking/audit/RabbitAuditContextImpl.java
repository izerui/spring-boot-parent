package com.ecworking.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;

/**
 * Created by serv on 2016/12/8.
 */
class RabbitAuditContextImpl implements AuditContext {

    private Logger logger = LoggerFactory.getLogger(RabbitAuditContextImpl.class);

    private RabbitTemplate rabbitTemplate;
    private RabbitAuditProperties rabbitmqProperties;

    public RabbitAuditContextImpl(RabbitTemplate rabbitTemplate, RabbitAuditProperties rabbitmqProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitmqProperties = rabbitmqProperties;
    }

    @Override
    @Async
    public void record(Record record) {
        try {
            rabbitTemplate.convertAndSend(rabbitmqProperties.getExchange(), rabbitmqProperties.getRoutingKey(), record);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
