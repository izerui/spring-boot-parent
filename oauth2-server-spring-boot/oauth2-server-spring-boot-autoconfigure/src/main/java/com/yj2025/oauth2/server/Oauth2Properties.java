package com.yj2025.oauth2.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oauth2.server")
public class Oauth2Properties {

    private String clientId = "ierp";
    private String clientSecret = "123456";
    private String redirectUri = "https://yj2025.com";
    private int accessTokenValiditySeconds = 7200;
    private int refreshTokenValiditySeconds = 86400;

}
