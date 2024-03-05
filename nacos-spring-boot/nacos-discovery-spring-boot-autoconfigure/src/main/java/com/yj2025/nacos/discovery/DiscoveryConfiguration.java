package com.yj2025.nacos.discovery;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Configuration
public class DiscoveryConfiguration {

    @Bean
    @ConditionalOnNacosDiscoveryEnabled
    public NacosDiscoveryProperties nacosProperties() {
        NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        // 启动时间
        metadata.put("startup.time",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        // home path
        metadata.put("boot.path", String.valueOf(new ApplicationHome(this.getClass()).getSource()));
        // pid
        metadata.put("boot.pid", new ApplicationPid().toString());
        // app version
        String appVersion = System.getenv("APP_VERSION");
        if (appVersion != null && !"".equals(appVersion)) {
            metadata.put("version", appVersion);
        }
        return nacosDiscoveryProperties;
    }

}
