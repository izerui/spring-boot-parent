package com.yj2025.customizer;

import com.yj2025.customizer.bean.BeanDefinitionRegistryCustomizer;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    // 方式一
    @Bean
    public BeanDefinitionRegistryCustomizer<CustomBean> originalBeanCustomizer() {
        return (registry, applicationContext) -> {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(CustomBean.class);
            beanDefinitionBuilder.setInitMethodName("init");
            registry.removeBeanDefinition("originalBean");
            registry.registerBeanDefinition("originalBean", beanDefinitionBuilder.getBeanDefinition());
        };
    }


    // 方式二 (注释掉方式一，该方式自动生效)
    @Bean
    public BeanDefinitionCustomizer customizer() {
        return bd -> {
            if (OriginalBean.class.getName().equals(bd.getBeanClassName())) {
                bd.setBeanClassName(CustomBean.class.getName());
                bd.setInitMethodName("init");
            }
        };
    }

    // 查看结果
    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory -> {
            Object originalBean = beanFactory.getBean("originalBean");
            System.out.println(originalBean);
        };
    }

}
