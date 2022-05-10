package com.yj2025.gateway;

import com.google.gson.Gson;
import com.yj2025.gateway.controller.TokenController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@EnableWebFluxSecurity
@Configuration
@Import({SecurityConfig.class, TokenController.class})
public class GatewayProxyConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Gson gson() {
        return new Gson();
    }
}
