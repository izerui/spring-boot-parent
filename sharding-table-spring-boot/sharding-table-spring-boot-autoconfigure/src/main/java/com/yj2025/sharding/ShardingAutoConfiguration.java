package com.yj2025.sharding;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShardingAutoConfiguration {

    private final ApplicationContext applicationContext;

    public ShardingAutoConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Bean
    public Sharding sharding() {
        return new Sharding(applicationContext);
    }
}
