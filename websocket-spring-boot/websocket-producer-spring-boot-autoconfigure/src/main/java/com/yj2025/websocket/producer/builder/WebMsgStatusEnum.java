package com.yj2025.websocket.producer.builder;

/**
 * 封装成业务主键，平台移除
 */
@Deprecated(since = "3.1", forRemoval = true)
public enum WebMsgStatusEnum {
    /**
     * 正在处理
     */
    PENDING,
    /**
     * 处理失败
     */
    ERROR,
    /**
     * 处理成功
     */
    SUCCESS,
}
