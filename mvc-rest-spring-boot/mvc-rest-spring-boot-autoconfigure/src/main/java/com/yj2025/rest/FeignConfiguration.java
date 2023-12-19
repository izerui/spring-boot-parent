package com.yj2025.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.Decoder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.encoding.BaseRequestInterceptor;
import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by serv on 2017/3/1.
 */
@Configuration
@EnableConfigurationProperties(FeignClientEncodingProperties.class)
@ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignAutoConfiguration")
public class FeignConfiguration {

    private final static List<String> PROXY_HEADER_NAMES = List.of(
            "entCode",
            "entName",
            "userCode",
            "userName",
            "accountCode",
            "accountName"
    );


    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Bean
    public Decoder feignDecoder(ObjectMapper objectMapper) {
        return new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverter(objectMapper)));
    }

    public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter(ObjectMapper objectMapper) {
        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper) {{
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"));
            mediaTypes.add(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"));
            setSupportedMediaTypes(mediaTypes);
        }});
        return () -> httpMessageConverters;
    }

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
                wrapHeaders(template);
                template.header(Constants.CLIENT_TYPE, Constants.FEIGN_REQUEST_TYPE);
                template.header(Constants.CLIENT_NAME, applicationName);
            }
        };
    }

    private void wrapHeaders(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (request != null) {
                Iterator<String> headerNameIterator = request.getHeaderNames().asIterator();
                headerNameIterator.forEachRemaining(headerName -> {
                    if (PROXY_HEADER_NAMES.contains(headerName)) {
                        template.header(headerName, request.getHeader(headerName));
                    }
                });
            }
        }
    }

}
