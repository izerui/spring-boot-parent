package com.yj2025.gateway;

import com.yj2025.gateway.controller.TokenKeyController;
import com.yj2025.gateway.filter.RelaxedQueryCharsWebServerCustomize;
import com.yj2025.gateway.security.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.function.client.WebClient;

@EnableWebFluxSecurity
@Configuration
@Import({SecurityConfiguration.class, TokenKeyController.class})
public class GatewayProxyConfiguration {

    @Autowired
    private GatewayProxyProperties properties;

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
