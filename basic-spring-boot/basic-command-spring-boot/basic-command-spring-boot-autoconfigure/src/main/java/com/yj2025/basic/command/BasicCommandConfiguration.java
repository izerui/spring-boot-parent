package com.yj2025.basic.command;

import com.yj2025.basic.support.Context;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BasicCommandConfiguration {

    @Bean
    public Context context(ApplicationContext applicationContext) {
        Context.applicationContext = applicationContext;
        return new Context();
    }


}
