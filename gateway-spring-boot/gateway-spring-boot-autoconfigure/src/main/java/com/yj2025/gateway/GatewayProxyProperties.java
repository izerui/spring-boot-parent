package com.yj2025.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author leiyang
 * @date 2021/8/6 10:25
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "gateway")
public class GatewayProxyProperties {

    private List<String> ignoredUrls;

    private String authAppName;

    private String client_id;

    private String client_secret;
}
