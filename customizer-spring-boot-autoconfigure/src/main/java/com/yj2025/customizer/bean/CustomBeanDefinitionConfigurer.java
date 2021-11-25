package com.yj2025.customizer.bean;

import org.springframework.context.ApplicationContext;

/**
 * 自定义的bean扩展基类，实现了该类的实例，并且注册为spring bean，则会自动根据返回的 BeanDefinitionContext 进行相应的bean定义的覆盖
 */
@FunctionalInterface
public interface CustomBeanDefinitionConfigurer {
    BeanDefinitionContext getBeanBeanDefinitionContext(ApplicationContext applicationContext);
}
