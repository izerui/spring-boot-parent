package com.yj2025.override;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OverrideConfiguration {

    @Bean
    public OverrideBeanPostProcessor registryPostProcessor() {
        return new OverrideBeanPostProcessor();
    }

}
