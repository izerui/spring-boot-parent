package com.yj2025.kafka;

import org.springframework.kafka.support.SendResult;

public interface ProducerCallback {

    void onSuccess(SendResult<String, Object> result);

    void onFailure(Throwable throwable);

}
