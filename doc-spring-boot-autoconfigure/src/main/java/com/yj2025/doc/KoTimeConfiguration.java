package com.yj2025.doc;

import cn.langpy.kotime.config.DefaultConfig;
import cn.langpy.kotime.config.LoadConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Configuration
@AutoConfigureBefore(LoadConfig.class)
public class KoTimeConfiguration {

    static {
        LoadConfig.log.setLevel(Level.OFF);
    }

    public static class OverrideDefaultConfig extends DefaultConfig {

        private Boolean overrideKoTimeEnable;
        private String overrideKoTimePointcut;

        public OverrideDefaultConfig(Boolean overrideKoTimeEnable, String overrideKoTimePointcut) {
            this.overrideKoTimeEnable = overrideKoTimeEnable;
            this.overrideKoTimePointcut = overrideKoTimePointcut;
        }

        public void init() {
            this.setEnable(true);
            this.setPointcut("execution(public * com.yj2025..*.*(..)) || execution(public * com.ecworking..*.*(..))");
            if (overrideKoTimeEnable != null) {
                this.setEnable(overrideKoTimeEnable);
            }
            if (overrideKoTimePointcut != null) {
                this.setPointcut(overrideKoTimePointcut);
            }
            this.setLogEnable(false);
            this.setLogLanguage("chinese");
            this.setThreshold(800.0);
            this.setExceptionEnable(true);
            this.setAuthEnable(false);
        }
    }

    /**
     * 覆盖 {@link DefaultConfig}
     */
    @Component
    public static class KoTimeConfigBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
            beanDefinitionRegistry.removeBeanDefinition("defaultConfig");
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(OverrideDefaultConfig.class);
            if (applicationContext.getEnvironment().getProperty("spring.profiles.active") != null && applicationContext.getEnvironment().getProperty("spring.profiles.active").contains("yunji")) {
                beanDefinitionBuilder.addConstructorArgValue(false);
            } else {
                beanDefinitionBuilder.addConstructorArgValue(applicationContext.getEnvironment().getProperty("override.ko-time.enable"));
            }
            beanDefinitionBuilder.addConstructorArgValue(applicationContext.getEnvironment().getProperty("override.ko-time.pointcut"));
            beanDefinitionBuilder.setInitMethodName("init");
            beanDefinitionRegistry.registerBeanDefinition("defaultConfig", beanDefinitionBuilder.getBeanDefinition());
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }
}
