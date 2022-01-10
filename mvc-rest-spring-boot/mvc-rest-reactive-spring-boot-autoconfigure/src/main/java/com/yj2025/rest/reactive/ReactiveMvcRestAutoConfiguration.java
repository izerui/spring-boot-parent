package com.yj2025.rest.reactive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.codec.ServerCodecConfigurer;

/**
 * Created by serv on 2016/10/18.
 */
@Configuration
@ConditionalOnWebApplication
@Import({CorsWebMvcConfiguration.class, FeignConfiguration.class})
public class ReactiveMvcRestAutoConfiguration {

    @Bean
    public GlobalErrorAttributes globalErrorAttributes() {
        return new GlobalErrorAttributes();
    }

    @Bean
    public GlobalErrorWebExceptionHandler globalErrorWebExceptionHandler(ServerCodecConfigurer serverCodecConfigurer, ResourceProperties resourceProperties, ApplicationContext applicationContext) {
        GlobalErrorWebExceptionHandler handler = new GlobalErrorWebExceptionHandler(globalErrorAttributes(), resourceProperties, applicationContext);
        handler.setMessageWriters(serverCodecConfigurer.getWriters());
        handler.setMessageReaders(serverCodecConfigurer.getReaders());
        return handler;
    }

}
