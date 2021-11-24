package com.yj2025.override;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public OverrideBeanDefinitionRegistry overrideBeanRegistry() {
        return applicationContext -> {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(OverrideBean.class);
            beanDefinitionBuilder.setInitMethodName("init");
            return new OverrideBeanDefinitionContext("originalBean", beanDefinitionBuilder.getBeanDefinition());
        };
    }

}
