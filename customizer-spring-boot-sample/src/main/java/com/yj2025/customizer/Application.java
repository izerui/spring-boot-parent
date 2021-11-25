package com.yj2025.customizer;

import com.yj2025.customizer.bean.BeanDefinitionContext;
import com.yj2025.customizer.bean.CustomBeanDefinitionConfigurer;
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


    // 方式一 (建议)
//    @Bean
//    public CustomBeanDefinitionConfigurer originalBeanConfigurer() {
//        return applicationContext -> {
//            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(CustomBean.class);
//            beanDefinitionBuilder.setInitMethodName("init");
//            return new BeanDefinitionContext("originalBean", beanDefinitionBuilder.getBeanDefinition());
//        };
//    }


    // 方式二
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
