package com.yj2025.gateway.proxy.security.rest;


import com.yj2025.gateway.proxy.GatewayProxyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 验证token通过auth服务器url地址验证,当auth服务异常导致请求无法继续(效率最低)
 */
@Configuration
@ConditionalOnProperty(name = "gateway.oauth2.auth-type", havingValue = "OPAQUE_REST")
public class OpaqueRestTokenConfiguration implements Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec> {

    @Autowired
    private GatewayProxyProperties properties;

    @LoadBalanced
    @Bean(name = "authLB")
    public WebClient.Builder authLb() {
        return WebClient.builder()
                .defaultHeaders(h -> h.setBasicAuth(properties.getOauth2().getClientId(), properties.getOauth2().getClientSecret()));
    }

    @Override
    public void customize(ServerHttpSecurity.OAuth2ResourceServerSpec auth) {
        auth.opaqueToken()
                .introspectionClientCredentials(properties.getOauth2().getClientId(), properties.getOauth2().getClientSecret())
                .introspector(new NimbusReactiveOpaqueTokenIntrospector(
                        "lb://" + properties.getOauth2().getAppName() + "/oauth/check_token",
                        authLb().build()
                ));
    }
}