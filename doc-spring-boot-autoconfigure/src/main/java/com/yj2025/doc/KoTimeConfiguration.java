package com.yj2025.doc;

import cn.langpy.kotime.config.DefaultConfig;
import cn.langpy.kotime.config.LoadConfig;
import com.yj2025.override.OverrideBeanDefinitionContext;
import com.yj2025.override.OverrideBeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.logging.Level;

@Configuration
public class KoTimeConfiguration {

    static {
        LoadConfig.log.setLevel(Level.OFF);
    }

    private final static String PRD_PROFILE_CONTAINS_STR = "yunji";


    @Bean
    public OverrideBeanDefinitionRegistry overrideBeanRegistry() {
        return applicationContext -> {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(OverrideDefaultConfig.class);
            String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
            // 确保线上初始不开启kotime,其他环境使用override配置
            if (activeProfiles != null && activeProfiles.length > 0 && Arrays.toString(activeProfiles).contains(PRD_PROFILE_CONTAINS_STR)) {
                beanDefinitionBuilder.addConstructorArgValue(Boolean.FALSE);
            } else {
                beanDefinitionBuilder.addConstructorArgValue(applicationContext.getEnvironment().getProperty("override.ko-time.enable"));
            }
            beanDefinitionBuilder.addConstructorArgValue(applicationContext.getEnvironment().getProperty("override.ko-time.pointcut"));
            beanDefinitionBuilder.setInitMethodName("init");
            return new OverrideBeanDefinitionContext("defaultConfig", beanDefinitionBuilder.getBeanDefinition());
        };
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

}
