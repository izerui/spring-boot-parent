package com.yj2025.cloud.file;

import com.yj2025.cloud.file.impl.CloudFileManagerImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CloudFileProperties.class)
public class CloudFileConfiguration {


    @Bean
    public CloudFileManager fileManager(CloudFileProperties properties) throws Exception{
        return new CloudFileManagerImpl(properties);
    }

}
