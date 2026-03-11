package com.yj2025.customizer;

import com.yj2025.customizer.bean.BeanDefinitionRegistryCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    // 方式一
    @Bean
    public BeanDefinitionRegistryCustomizer originalBeanCustomizer() {
        return (registry, applicationContext) -> {
            log.info("使用 com.yj2025.customizer.bean.BeanDefinitionRegistryCustomizer 方式进行bean替换");
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(CustomBean.class);
            beanDefinitionBuilder.setInitMethodName("init");
            registry.removeBeanDefinition("originalBean");
            registry.registerBeanDefinition("originalBean", beanDefinitionBuilder.getBeanDefinition());
        };
    }


    // 方式二 (注释掉方式一，该方式自动生效)
    @Bean
    public BeanDefinitionCustomizer customizer() {
        log.info("使用 org.springframework.beans.factory.config.BeanDefinitionCustomizer 方式进行bean替换");
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
