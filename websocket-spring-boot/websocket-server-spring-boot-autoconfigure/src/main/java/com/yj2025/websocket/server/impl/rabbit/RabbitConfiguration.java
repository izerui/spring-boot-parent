package com.yj2025.websocket.server.impl.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.websocket.WebMsg;
import com.yj2025.websocket.server.WebSocketServerProperties;
import com.yj2025.websocket.server.impl.UserChannelService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@ConditionalOnProperty(name = "websocket.server.listener-type", matchIfMissing = true, havingValue = "rabbit")
public class RabbitConfiguration {

    private final static String SERVER_RANDOM_CHARS = "websocket-server-" + RandomStringUtils.randomAlphabetic(10);

    @Autowired
    private UserChannelService userChannelService;
    @Autowired
    private ObjectMapper objectMapper;


    public String getQueueName() {
        return SERVER_RANDOM_CHARS;
    }

    /**
     * 监听消息并发送给js
     */
    @Transactional
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "#{rabbitConfiguration.getQueueName()}", durable = "false", autoDelete = "true"),
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
