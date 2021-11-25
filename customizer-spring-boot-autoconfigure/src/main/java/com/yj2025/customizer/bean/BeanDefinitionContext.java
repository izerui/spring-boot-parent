package com.yj2025.customizer.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.config.BeanDefinition;

@Data
@AllArgsConstructor
public class BeanDefinitionContext {
    private String beanName;
    private BeanDefinition beanDefinition;
}
