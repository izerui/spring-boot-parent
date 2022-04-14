package com.yj2025.nacos.discovery;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Configuration
public class DiscoveryConfiguration {

    @Bean
    public NacosDiscoveryProperties nacosProperties() {
        NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        metadata.put("startup.time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date()));
        return nacosDiscoveryProperties;
    }

}
