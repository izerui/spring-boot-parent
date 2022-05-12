package com.yj2025.amazonaws;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuyuhua
 * @date 2022/4/15
 */
@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public class AwsAutoConfiguration {

    @Bean
    public AwsServiceFactory awsClientFactory(AwsProperties properties) {
        return new AwsServiceFactory(properties);
    }
}
