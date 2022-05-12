package com.yj2025.customizer.bean;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

/**
 * 自定义的bean扩展基类，实现了该类的实例，并且注册为spring bean，并暴露 BeanDefinitionRegistry 钩子
 */
@FunctionalInterface
public interface BeanDefinitionRegistryCustomizer<T> {
    void customize(BeanDefinitionRegistry registry, ApplicationContext applicationContext);
}
