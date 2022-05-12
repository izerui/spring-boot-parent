package com.yj2025.gateway;

import com.yj2025.gateway.controller.TokenKeyController;
import com.yj2025.gateway.filter.RelaxedQueryCharsWebServerCustomize;
import com.yj2025.gateway.security.ServerSecurityConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.function.client.WebClient;

@EnableWebFluxSecurity
@Configuration
@Import({ServerSecurityConfiguration.class, TokenKeyController.class})
public class GatewayProxyConfiguration {

    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public RelaxedQueryCharsWebServerCustomize relaxedQueryCharsWebServerCustomize() {
        return new RelaxedQueryCharsWebServerCustomize();
    }
}
