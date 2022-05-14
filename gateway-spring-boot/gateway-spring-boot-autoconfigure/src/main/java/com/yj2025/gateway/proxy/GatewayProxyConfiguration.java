package com.yj2025.gateway.proxy;

import com.yj2025.gateway.proxy.controller.ProxyQrcodeController;
import com.yj2025.gateway.proxy.controller.ProxyTokenKeyController;
import com.yj2025.gateway.proxy.filter.RelaxedQueryCharsWebServerCustomize;
import com.yj2025.gateway.proxy.security.SecurityConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.function.client.WebClient;

@EnableWebFluxSecurity
@Configuration
@EnableConfigurationProperties(GatewayProxyProperties.class)
@Import({SecurityConfiguration.class, ProxyTokenKeyController.class, ProxyQrcodeController.class})
public class GatewayProxyConfiguration {

    @LoadBalanced
    @Bean
    @Primary
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public RelaxedQueryCharsWebServerCustomize relaxedQueryCharsWebServerCustomize() {
        return new RelaxedQueryCharsWebServerCustomize();
    }
}
