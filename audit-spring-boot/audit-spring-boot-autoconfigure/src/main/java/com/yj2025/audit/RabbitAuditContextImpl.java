package com.yj2025.audit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

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
    public void record(Record record) {
        if (record == null || record.getName() == null) {
            return;
        }
        String exchange = rabbitmqProperties.getExchange();
        String routingKey = rabbitmqProperties.getRoutingKey();
        if (StringUtils.isBlank(exchange) || StringUtils.isBlank(routingKey)) {
            return;
        }
        try {
            Message message = rabbitTemplate.getMessageConverter()
                    .toMessage(record,
                            MessagePropertiesBuilder.newInstance()
                                    .setHeader("x-message-ttl", 60000)
                                    .build()
                    );
            rabbitTemplate.send(exchange, routingKey, message);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
