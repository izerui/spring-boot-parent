package com.yj2025.rest.reactive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Created by serv on 2017/2/16.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "rest.cors.allowed", havingValue = "true")
public class CorsWebMvcConfiguration implements WebFluxConfigurer {

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


}
