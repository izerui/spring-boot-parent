package com.yj2025.gateway.proxy.security.jwt;

import com.yj2025.gateway.proxy.GatewayProxyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * jwt token配置，解析jwt - token进行验证(效率最高)
 */
@Configuration
@ConditionalOnProperty(name = "gateway.oauth2.auth-type", havingValue = "JWT")
public class JwtTokenConfiguration implements Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec> {

    @Autowired
    private GatewayProxyProperties properties;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public void customize(ServerHttpSecurity.OAuth2ResourceServerSpec auth) {
        // 注意： 当auth服务更新jwt证书后，需要重启当前网关服务，因为jwkSetUri只调用一次，即缓存到服务内存中。
        auth.jwt()
                .jwtDecoder(
                        NimbusReactiveJwtDecoder
                                .withJwkSetUri("lb://" + properties.getOauth2().getAppName() + "/rsa/key")
                                .webClient(webClientBuilder.build())
                                .build()
                );
    }
}