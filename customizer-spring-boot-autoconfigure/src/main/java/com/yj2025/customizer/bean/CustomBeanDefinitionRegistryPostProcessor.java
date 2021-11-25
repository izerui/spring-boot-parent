package com.yj2025.customizer.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

/**
 * bean 初始化前置定义处理器 {@link org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory, List)}
 * <ul>
 *     <li>优先进一步注册bean定义: {@link BeanDefinitionRegistryPostProcessor}</li>
 *     <li>{@link BeanFactoryPostProcessor}</li>
 * </ul>
 */
@Slf4j
public class CustomBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 扩展方式一：覆盖bean定义
     * 示例：
     * <code>
     * @Bean
     * public static CustomBeanDefinitionConfigurer originalBeanConfigurer() {
     * return applicationContext -> {
     *     BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(CustomBean.class);
     *         beanDefinitionBuilder.setInitMethodName("init");
     *         return new BeanDefinitionContext("originalBean", beanDefinitionBuilder.getBeanDefinition());
     *     };
     * }
     * </code>
     *
     * @param beanDefinitionRegistry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // 找到所有定义的bean覆盖定义
        Map<String, CustomBeanDefinitionConfigurer> beansOfType = applicationContext.getBeansOfType(CustomBeanDefinitionConfigurer.class);
        for (CustomBeanDefinitionConfigurer customBeanDefinitionRegistry : beansOfType.values()) {
            BeanDefinitionContext beanDefinitionContext = customBeanDefinitionRegistry.getBeanBeanDefinitionContext(applicationContext);
            // 替换bean定义
            if (beanDefinitionRegistry.containsBeanDefinition(beanDefinitionContext.getBeanName())) {
                beanDefinitionRegistry.removeBeanDefinition(beanDefinitionContext.getBeanName());
                beanDefinitionRegistry.registerBeanDefinition(beanDefinitionContext.getBeanName(), beanDefinitionContext.getBeanDefinition());
            } else {
                log.warn("Bean: {} Not found, Skip Override!", beanDefinitionContext.getBeanName());
            }
        }
    }

    /**
     * 扩展方式二： 支持被spring bean管理的 BeanDefinitionCustomizer 实例，个性化每个bean定义
     * <code>
     * @Bean
     * public static BeanDefinitionCustomizer customizer() {
     *     return bd -> {
     *         if (OriginalBean.class.getName().equals(bd.getBeanClassName())) {
     *             bd.setBeanClassName(CustomBean.class.getName());
     *             bd.setInitMethodName("init");
     *         }
     *     };
     * }
     * </code>
     * @param configurableListableBeanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        String[] beanDefinitionNames = configurableListableBeanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanDefinitionName);
            for (BeanDefinitionCustomizer customizer : applicationContext.getBeansOfType(BeanDefinitionCustomizer.class).values()) {
                customizer.customize(beanDefinition);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
