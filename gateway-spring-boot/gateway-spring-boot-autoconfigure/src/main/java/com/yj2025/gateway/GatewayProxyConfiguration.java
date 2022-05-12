package com.yj2025.gateway;

import com.yj2025.gateway.controller.TokenKeyController;
import com.yj2025.gateway.filter.RelaxedQueryCharsWebServerCustomize;
import com.yj2025.gateway.security.ServerSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@EnableWebFluxSecurity
@Configuration
@Import({ServerSecurityConfig.class, TokenKeyController.class})
public class GatewayProxyConfiguration {

    @Bean
    public RelaxedQueryCharsWebServerCustomize relaxedQueryCharsWebServerCustomize() {
        return new RelaxedQueryCharsWebServerCustomize();
    }
}
