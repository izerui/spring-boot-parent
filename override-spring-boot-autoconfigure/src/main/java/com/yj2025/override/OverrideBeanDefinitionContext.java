package com.yj2025.override;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.config.BeanDefinition;

@Data
@AllArgsConstructor
public class OverrideBeanDefinitionContext {
    private String beanName;
    private BeanDefinition beanDefinition;
}
