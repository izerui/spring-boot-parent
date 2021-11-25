package com.yj2025.customizer;

import com.yj2025.customizer.bean.CustomBeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomConfiguration {

    @Bean
    public static CustomBeanDefinitionRegistryPostProcessor registryPostProcessor() {
        return new CustomBeanDefinitionRegistryPostProcessor();
    }

}
