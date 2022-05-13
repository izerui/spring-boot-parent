package com.yj2025.gateway.security.jwt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;

/**
 * jwt token配置，解析jwt - token进行验证(效率最高)
 */
@Configuration
@ConditionalOnProperty(name = "gateway.oauth2.auth-type", havingValue = "JWT")
public class JwtTokenConfiguration implements Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec> {

    @Override
    public void customize(ServerHttpSecurity.OAuth2ResourceServerSpec auth) {
        auth.jwt().jwkSetUri("http://localhost:8080/rsa/key");
    }
}