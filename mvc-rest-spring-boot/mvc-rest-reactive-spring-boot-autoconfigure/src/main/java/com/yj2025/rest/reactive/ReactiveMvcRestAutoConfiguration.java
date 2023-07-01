package com.yj2025.rest.reactive;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

/**
 * Created by serv on 2016/10/18.
 */
@Configuration
@ConditionalOnWebApplication
@AutoConfigureBefore(ErrorWebFluxAutoConfiguration.class)
@Import({WebMvcConfiguration.class, FeignConfiguration.class})
public class ReactiveMvcRestAutoConfiguration {

    @Bean
    public GlobalErrorAttributes globalErrorAttributes() {
        return new GlobalErrorAttributes();
    }

    @Bean
    public GlobalErrorWebExceptionHandler globalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                                                         WebProperties webProperties, ObjectProvider<ViewResolver> viewResolvers,
                                                                         ServerCodecConfigurer serverCodecConfigurer, ApplicationContext applicationContext) {
        GlobalErrorWebExceptionHandler exceptionHandler = new GlobalErrorWebExceptionHandler(errorAttributes,
                webProperties.getResources(), applicationContext);
        exceptionHandler.setViewResolvers(viewResolvers.orderedStream().toList());
        exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }

}
