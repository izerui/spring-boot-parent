package com.yj2025.kafka;

import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface MessageProducer {

    /**
     * 发送消息到kafka
     *
     * @param topic
     * @param key
     * @param value
     */
    CompletableFuture<SendResult<String, Object>> send(String topic, String key, @Nullable Object value);

    /**
     * 发送消息到kafka,并监听回调事件
     *
     * @param topic
     * @param key
     * @param value
     * @param callback
     */
    void send(String topic, String key, @Nullable Object value, ProducerCallback callback);


    /**
     * 发送消息到kafka,指定header头信息
     *
     * @param topic
     * @param key
     * @param value
     * @param headers
     */
    CompletableFuture<SendResult<String, Object>> send(String topic, String key, @Nullable Object value, Map<String, String> headers);


    /**
     * 发送消息到kafka,指定header头信息, 并监听回调事件
     * @param topic
     * @param key
     * @param value
     * @param headers
     * @param callback
     */
    void send(String topic, String key, @Nullable Object value, Map<String, String> headers, ProducerCallback callback);

}
