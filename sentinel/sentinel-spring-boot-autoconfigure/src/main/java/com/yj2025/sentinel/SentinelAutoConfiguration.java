package com.yj2025.sentinel;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class SentinelAutoConfiguration {

    @ConditionalOnClass(name = "io.swagger.annotations.ApiOperation")
    @Bean
    public SentinelResAspect sentinelResAspect() {
        return new SentinelResAspect();
    }
}
