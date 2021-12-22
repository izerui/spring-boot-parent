package com.yj2025.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by serv on 2016/10/18.
 */
@Configuration
@ConditionalOnWebApplication
@Import({CorsWebMvcConfiguration.class, FeignConfiguration.class})
public class MvcRestAutoConfiguration extends WebMvcConfigurerAdapter{

    @Bean
    @ConditionalOnMissingBean
    public GlobResponseBodyAdviceAdapter globRequestBodyAdviceAdapter(){
        return new GlobResponseBodyAdviceAdapter();
    }

}
