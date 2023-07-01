package com.yj2025.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by serv on 2016/10/18.
 */
@Configuration
@ConditionalOnWebApplication
@Import({WebMvcConfiguration.class, FeignConfiguration.class})
public class MvcRestAutoConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public GlobResponseBodyAdviceAdapter globRequestBodyAdviceAdapter(ErrorAttributes errorAttributes) {
        return new GlobResponseBodyAdviceAdapter(errorAttributes);
    }

}
