package com.yj2025.override;

import org.springframework.context.ApplicationContext;

/**
 * 覆盖bean定义
 */
@FunctionalInterface
public interface OverrideBeanDefinitionRegistry {
    OverrideBeanDefinitionContext getBeanBeanRegistry(ApplicationContext applicationContext);
}
