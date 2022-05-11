package com.yj2025.oauth2.server;

import com.yj2025.oauth2.server.advice.GlobalResponseBodyAdviceAdapter;
import com.yj2025.oauth2.server.security.Oauth2ServerConfig;
import com.yj2025.oauth2.server.security.ServerSecurityConfig;
import com.yj2025.oauth2.server.controller.TokenController;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(Oauth2Properties.class)
@Import({ServerSecurityConfig.class, Oauth2ServerConfig.class, TokenController.class})
public class Oauth2ServerConfiguration {

    @Bean
    public GlobalResponseBodyAdviceAdapter globalResponseBodyAdviceAdapter(ErrorAttributes errorAttributes) {
        return new GlobalResponseBodyAdviceAdapter(errorAttributes);
    }

}
