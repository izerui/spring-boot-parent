package com.yj2025.websocket.producer.impl;

import com.yj2025.websocket.WebMsg;
import com.yj2025.websocket.producer.WebSocketContext;
import com.yj2025.websocket.producer.WebSocketProducerProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liuyuhua
 */
public class RabbitContextImpl implements WebSocketContext, DisposableBean {

    private RabbitTemplate rabbitTemplate;
    private WebSocketProducerProperties rabbitWebSocketProperties;
    private static final ThreadPoolExecutor POOL_EXECUTOR = new ThreadPoolExecutor(10, 20, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(65536), new ThreadPoolExecutor.CallerRunsPolicy());

    public RabbitContextImpl(RabbitTemplate rabbitTemplate, WebSocketProducerProperties rabbitWebSocketProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitWebSocketProperties = rabbitWebSocketProperties;
    }

    @Override
    public void sendMessage(WebMsg webMsg) {
        rabbitTemplate.convertAndSend(rabbitWebSocketProperties.getRabbit().getExchange(), rabbitWebSocketProperties.getRabbit().getRoutingKey(), webMsg);
    }

    @Override
    public void sendMessageAsync(WebMsg webMsg) {
        POOL_EXECUTOR.execute(() -> {
            rabbitTemplate.convertAndSend(rabbitWebSocketProperties.getRabbit().getExchange(), rabbitWebSocketProperties.getRabbit().getRoutingKey(), webMsg);
        });
    }

    @Override
    public void destroy() throws Exception {
        POOL_EXECUTOR.shutdown();
    }
}
