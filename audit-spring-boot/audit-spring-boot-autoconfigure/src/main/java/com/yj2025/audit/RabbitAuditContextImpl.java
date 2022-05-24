package com.yj2025.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by serv on 2016/12/8.
 */
@Transactional
class RabbitAuditContextImpl implements AuditContext {

    private Logger logger = LoggerFactory.getLogger(RabbitAuditContextImpl.class);

    private RabbitTemplate rabbitTemplate;
    private RabbitAuditProperties rabbitmqProperties;

    public RabbitAuditContextImpl(RabbitTemplate rabbitTemplate, RabbitAuditProperties rabbitmqProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitmqProperties = rabbitmqProperties;
    }

    @Override
    public void record(Record record) {
        try {
            Message message = rabbitTemplate.getMessageConverter()
                    .toMessage(record,
                            MessagePropertiesBuilder.newInstance()
                                    .setHeader("x-message-ttl", 60000)
                                    .build()
                    );
            rabbitTemplate.send(rabbitmqProperties.getExchange(), rabbitmqProperties.getRoutingKey(), message);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
