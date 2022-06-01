package com.yj2025.open.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPublicKey;

@Data
@ConfigurationProperties("open.gateway")
public class GatewayProperties {

    private String oauthApp;
}
