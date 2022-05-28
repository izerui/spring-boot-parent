package com.yj2025.websocket.server.impl.rabbit;

import org.apache.commons.lang3.RandomStringUtils;

public class RabbitQueueName {

    // https://zhuanlan.zhihu.com/p/167826668 参考设置相应的队列参数
    private final static String SERVER_RANDOM_CHARS = "websocket-server-" + RandomStringUtils.randomAlphabetic(10);

    public String getQueueName() {
        return SERVER_RANDOM_CHARS;
    }

}
