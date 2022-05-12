package com.yj2025.oauth2.server.security;

import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

public interface ExpandEndpointsConfigurer {

    void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception;
}
