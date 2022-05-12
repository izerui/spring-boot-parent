package com.yj2025.gateway.security.authorization;

import com.yj2025.gateway.PathMatcherAuthoritiesLoader;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.web.server.authorization.AuthorizationContext;

public class AuthorizationManagerFactory {

    private PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader;

    public AuthorizationManagerFactory(PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader) {
        this.pathMatcherAuthoritiesLoader = pathMatcherAuthoritiesLoader;
    }

    public ReactiveAuthorizationManager<AuthorizationContext> getAuthorizationManager(boolean jwtEnabled) {
        return jwtEnabled ? new OpaqueAuthorizationManager(pathMatcherAuthoritiesLoader) : new JwtAuthorizationManager(pathMatcherAuthoritiesLoader);
    }
}
