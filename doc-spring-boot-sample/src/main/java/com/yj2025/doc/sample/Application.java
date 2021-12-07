package com.yj2025.doc.sample;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // 查看结果
    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory -> {
            String[] beanNamesForType = beanFactory.getBeanNamesForType(Application.class);
            for (String s : beanNamesForType) {
                System.out.println(s);
            }
        };
    }

}
