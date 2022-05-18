package com.yj2025.websocket.server.impl.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.websocket.WebMsg;
import com.yj2025.websocket.server.impl.UserChannelService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

public class RabbitMessageListener {

    private ObjectMapper objectMapper;
    private UserChannelService userChannelService;

    public RabbitMessageListener(ObjectMapper objectMapper, UserChannelService userChannelService) {
        this.objectMapper = objectMapper;
        this.userChannelService = userChannelService;
    }

    /**
     * 监听消息并发送给js
     */
    @Transactional
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "#{queueName.getQueueName()}", durable = "false", autoDelete = "true"),
                    key = {"#{webSocketServerProperties.rabbit.routingKey}"},
                    exchange = @Exchange(
                            value = "#{webSocketServerProperties.rabbit.exchange}",
                            type = "topic"))
    )
    public void process(Message<byte[]> message) throws Exception {
        WebMsg webMsg = objectMapper.readValue(message.getPayload(), WebMsg.class);
        userChannelService.sendToConsumer(webMsg);
    }

}
