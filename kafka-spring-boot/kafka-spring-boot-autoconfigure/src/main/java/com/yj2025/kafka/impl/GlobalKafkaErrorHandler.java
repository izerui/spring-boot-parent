package com.yj2025.kafka.impl;

import com.yj2025.kafka.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GlobalKafkaErrorHandler implements CommonErrorHandler {

    private MessageProducer messageProducer;

    private final DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler();

    public GlobalKafkaErrorHandler(MessageProducer messageProducer){
        this.messageProducer = messageProducer;
    }
    @Override
    public boolean handleOne(Exception thrownException, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer,
                             MessageListenerContainer container) {
        log.error("'handleRecord' is not implemented by this handler", thrownException);
        boolean b = defaultErrorHandler.handleOne(thrownException, record, consumer, container);
        if (b) {
            try {
                Map<String, Object> messageBody = new HashMap<>();
                messageBody.put("topic", record.topic());
                messageBody.put("partition", record.partition());
                messageBody.put("offset", record.offset());
                messageBody.put("key", record.key());
                messageBody.put("value", record.value());
                messageBody.put("timestamp", record.timestamp());
                Map<String, String> headers = new HashMap<>();
                record.headers().forEach(header -> {
                    headers.put(header.key(), new String(header.value()));
                });
                messageBody.put("header", headers);
                messageProducer.send(KafkaErrorMessageConfig.topic, KafkaErrorMessageConfig.key, messageBody, headers);
            } catch (Exception e) {
                log.error("send 'unchecked.dead' message error", thrownException);
            }
        }
        return b;
    }
}
