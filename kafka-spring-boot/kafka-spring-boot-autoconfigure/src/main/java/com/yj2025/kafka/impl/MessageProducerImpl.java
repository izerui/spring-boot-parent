package com.yj2025.kafka.impl;

import com.yj2025.kafka.MessageProducer;
import com.yj2025.kafka.ProducerCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@Slf4j
public class MessageProducerImpl implements MessageProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final String applicationName;

    public MessageProducerImpl(KafkaTemplate<String, Object> kafkaTemplate, KafkaProperties kafkaProperties, String applicationName) {

        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
        this.applicationName = applicationName;
    }

    private void wrapHeaders(Headers headers) {
        BiFunction<Object, Object, byte[]> toBytesFun = (obj1, obj2) -> {
            Object obj = obj1 != null ? obj1 : obj2;
            if (obj == null) {
                return null;
            } else {
                return String.valueOf(obj).getBytes(StandardCharsets.UTF_8);
            }
        };
        LocalDateTime now = LocalDateTime.now();
        headers.add("sendTime", toBytesFun.apply(now.toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), null));
        headers.add("sendTimeLabel", toBytesFun.apply(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        headers.add("applicationName", toBytesFun.apply(applicationName, null));
        headers.add("producerClientId", toBytesFun.apply(kafkaProperties.getProducer().getClientId(), kafkaProperties.getClientId()));
        headers.add("messageUuid", UUID.randomUUID().toString().getBytes());
    }

    public CompletableFuture<SendResult<String, Object>> send(String topic, String key, @Nullable Object value, Map<String, String> headers) {
        Assert.notNull(topic, "topic不能为空");
        Assert.notNull(key, "key不能为空");
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic, key, value);
        headers.forEach((s, s2) -> {
            if (!List.of("sendTime","sendTimeLabel","applicationName","producerClientId","messageUuid").contains(s)){
                producerRecord.headers().add(s, s2.getBytes(StandardCharsets.UTF_8));
            }
        });
        wrapHeaders(producerRecord.headers());
        return kafkaTemplate.send(producerRecord);
    }


    public CompletableFuture<SendResult<String, Object>> send(String topic, String key, @Nullable Object value) {
        return this.send(topic, key, value, new HashMap<>());
    }


    public void send(String topic, String key, @Nullable Object value, ProducerCallback callback) {
        CompletableFuture<SendResult<String, Object>> completableFuture = this.send(topic, key, value);
        callback(completableFuture, callback);
    }

    public void send(String topic, String key, @Nullable Object value, Map<String, String> headers, ProducerCallback callback) {
        CompletableFuture<SendResult<String, Object>> completableFuture = this.send(topic, key, value, headers);
        callback(completableFuture, callback);
    }


    private void callback(CompletableFuture<SendResult<String, Object>> completableFuture, ProducerCallback callback) {
        completableFuture.thenAccept(callback::onSuccess);
        completableFuture.exceptionally(throwable -> {
            callback.onFailure(throwable);
            return null;
        });
    }

}
