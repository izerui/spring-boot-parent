package com.yj2025.command;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {

    @Bean
    public Context context() {
        return new Context();
    }

}
