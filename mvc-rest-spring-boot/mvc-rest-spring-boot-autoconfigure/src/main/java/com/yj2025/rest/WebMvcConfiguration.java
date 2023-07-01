package com.yj2025.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by serv on 2017/2/16.
 */
@Configuration
@ConditionalOnWebApplication
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 全局允许跨域
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*");
    }

    /**
     * 升级boot 3.1.1后暂时兼容写法 地址类似： /a /a/
     *
     * @param configurer
     */
    @Deprecated
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }

}
