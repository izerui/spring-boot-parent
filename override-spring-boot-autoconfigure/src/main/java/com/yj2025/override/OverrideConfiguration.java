package com.yj2025.override;

import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OverrideConfiguration {

    @Bean
    public BeanDefinitionRegistryPostProcessor registryPostProcessor() {
        return new OverrideBeanPostProcessor();
    }

}
