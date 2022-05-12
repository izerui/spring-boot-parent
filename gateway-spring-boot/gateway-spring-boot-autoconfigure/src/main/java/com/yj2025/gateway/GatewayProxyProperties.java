package com.yj2025.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "gateway")
public class GatewayProxyProperties {

    private boolean maintenance;
    private String whitelistIp;
    private String[] ignoredUrls;
    private Oauth2Properties oauth2;

    @Data
    public static class Oauth2Properties {

        private String appName;
        private String clientId;
        private String clientSecret;
        private boolean jwtEnabled = false;
    }

}
