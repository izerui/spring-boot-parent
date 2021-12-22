package com.yj2025.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.RetryableException;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.encoding.BaseRequestInterceptor;
import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by serv on 2017/3/1.
 */
@Configuration
@EnableConfigurationProperties(FeignClientEncodingProperties.class)
@ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignAutoConfiguration")
public class FeignConfiguration {


    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Bean
    public Retryer retryer() {
        return new Retryer() {
            private String name = "never-retryer";

            @Override
            public void continueOrPropagate(RetryableException e) {
                throw e;
            }

            @Override
            public Retryer clone() {
                return this;
            }
        };
    }

    @Bean
    public FeignErrorDecoder errorDecoder() {
        return new FeignErrorDecoder(new ObjectMapper());
    }

    @Bean
    public RequestInterceptor requestInterceptor(FeignClientEncodingProperties feignClientEncodingProperties) {
        return new BaseRequestInterceptor(feignClientEncodingProperties) {
            @Override
            public void apply(RequestTemplate template) {
                template.header(Constants.CLIENT_TYPE, Constants.FEIGN_REQUEST_TYPE);
                template.header(Constants.CLIENT_NAME, applicationName);
            }
        };
    }

}
