package com.yj2025.basic.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BasicWebAutoConfiguration {

    @Bean
    public WebRequestContext webRequestContext() {
        return new WebRequestContext();
    }
}
