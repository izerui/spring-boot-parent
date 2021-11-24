package com.yj2025.override;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * bean 初始化前置处理器
 * <ul>
 *     <li>优先进一步注册bean定义: {@link BeanDefinitionRegistryPostProcessor}</li>
 *     <li>{@link BeanFactoryPostProcessor}</li>
 * </ul>
 *
 */
@Slf4j
public class OverrideBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // 找到所有定义的bean覆盖定义
        Map<String, OverrideBeanDefinitionRegistry> beansOfType = applicationContext.getBeansOfType(OverrideBeanDefinitionRegistry.class);
        for (OverrideBeanDefinitionRegistry overrideBeanRegistry : beansOfType.values()) {
            OverrideBeanDefinitionContext beanDefinitionContext = overrideBeanRegistry.getBeanBeanRegistry(applicationContext);
            // 替换bean定义
            if (beanDefinitionRegistry.containsBeanDefinition(beanDefinitionContext.getBeanName())) {
                beanDefinitionRegistry.removeBeanDefinition(beanDefinitionContext.getBeanName());
                beanDefinitionRegistry.registerBeanDefinition(beanDefinitionContext.getBeanName(), beanDefinitionContext.getBeanDefinition());
            } else {
                log.warn("Bean: {} Not found, Skip Override!", beanDefinitionContext.getBeanName());
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
        System.out.println(defaultListableBeanFactory);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
