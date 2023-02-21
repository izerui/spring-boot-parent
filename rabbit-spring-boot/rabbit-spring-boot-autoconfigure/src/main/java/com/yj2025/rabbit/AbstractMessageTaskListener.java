package com.yj2025.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class AbstractMessageTaskListener {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageTaskListener.class);

    private ObjectMapper objectMapper;

    public AbstractMessageTaskListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected void createTask(String taskName, Message message, Consumer<SourceMessageVO> consumer) {
        String body = "";
        try {
            body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info(taskName + "收到消息：{}", body);
            SourceMessageVO messageVO = objectMapper.readValue(body, SourceMessageVO.class);
            consumer.accept(messageVO);
        } catch (Exception var6) {
            log.error(taskName + "收到消息，发生异常：{}, \n 消息内容:\n{}", var6, body);
            throw new AmqpRejectAndDontRequeueException(body, var6);
        }
    }
}
