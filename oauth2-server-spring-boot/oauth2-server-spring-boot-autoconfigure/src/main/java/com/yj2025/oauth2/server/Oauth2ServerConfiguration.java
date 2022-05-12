package com.yj2025.oauth2.server;

import com.yj2025.oauth2.server.controller.TokenController;
import com.yj2025.oauth2.server.security.AuthorizationServerConfiguration;
import com.yj2025.oauth2.server.security.SecurityConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(Oauth2Properties.class)
@Import({SecurityConfiguration.class, AuthorizationServerConfiguration.class, TokenController.class})
public class Oauth2ServerConfiguration {

}
