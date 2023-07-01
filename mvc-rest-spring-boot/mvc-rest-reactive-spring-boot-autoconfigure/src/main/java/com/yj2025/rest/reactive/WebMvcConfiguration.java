package com.yj2025.rest.reactive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Created by serv on 2017/2/16.
 */
@Configuration
@ConditionalOnWebApplication
public class WebMvcConfiguration implements WebFluxConfigurer {

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
     * 升级boot 3.1.1后暂时兼容写法
     *
     * @param configurer
     */
    @Deprecated
    @Override
    public void configurePathMatching(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }


}
