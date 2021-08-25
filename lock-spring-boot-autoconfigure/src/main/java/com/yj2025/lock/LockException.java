package com.yj2025.lock;

/**
 * Created by serv on 16/8/16.
 */
public class LockException extends RuntimeException{

    public LockException() {
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }
}
