package com.yj2025.sentinel;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentinelAutoConfiguration {

    @ConditionalOnClass(name = "org.springframework.amqp.rabbit.annotation.RabbitListener")
    @Bean
    public SentinelMqAspect sentinelResAspect() {
        return new SentinelMqAspect();
    }

    @ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignClient")
    @Bean
    public SentinelFeignAspect sentinelFeignAspect() {
        return new SentinelFeignAspect();
    }
}
