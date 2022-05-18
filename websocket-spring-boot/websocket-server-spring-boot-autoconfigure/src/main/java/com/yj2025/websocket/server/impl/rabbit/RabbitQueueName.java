package com.yj2025.websocket.server.impl.rabbit;

import org.apache.commons.lang3.RandomStringUtils;

public class RabbitQueueName {

    private final static String SERVER_RANDOM_CHARS = "websocket-server-" + RandomStringUtils.randomAlphabetic(10);

    public String getQueueName() {
        return SERVER_RANDOM_CHARS;
    }

}
