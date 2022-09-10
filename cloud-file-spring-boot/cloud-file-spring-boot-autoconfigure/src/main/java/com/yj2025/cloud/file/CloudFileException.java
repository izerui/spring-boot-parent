package com.yj2025.cloud.file;

public class CloudFileException extends RuntimeException {

    public CloudFileException(String message) {
        super(message);
    }

    public CloudFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
