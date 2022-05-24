package com.yj2025.disruptor;

/**
 * @author liuyuhua
 * @date 2022/5/24
 */
public class DisruptorException extends RuntimeException{
    public DisruptorException() {
        super();
    }

    public DisruptorException(String message) {
        super(message);
    }

    public DisruptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DisruptorException(Throwable cause) {
        super(cause);
    }
}
