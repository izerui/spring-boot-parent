package com.yj2025.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TenantAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public TenantMethodAspect repositoryQueryAspect() {
        return new TenantMethodAspect(applicationContext);
    }
}
