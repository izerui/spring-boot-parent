package com.yj2025.rest.reactive;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
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
@AutoConfigureBefore(ErrorWebFluxAutoConfiguration.class)
@Import({CorsWebMvcConfiguration.class, FeignConfiguration.class})
public class ReactiveMvcRestAutoConfiguration {

    @Bean
    public GlobalErrorAttributes globalErrorAttributes() {
        return new GlobalErrorAttributes();
    }

    @Bean
    public GlobalErrorWebExceptionHandler globalErrorWebExceptionHandler(ServerCodecConfigurer serverCodecConfigurer, WebProperties.Resources resources, ApplicationContext applicationContext) {
        GlobalErrorWebExceptionHandler handler = new GlobalErrorWebExceptionHandler(globalErrorAttributes(), resources, applicationContext);
        handler.setMessageWriters(serverCodecConfigurer.getWriters());
        handler.setMessageReaders(serverCodecConfigurer.getReaders());
        return handler;
    }

}
