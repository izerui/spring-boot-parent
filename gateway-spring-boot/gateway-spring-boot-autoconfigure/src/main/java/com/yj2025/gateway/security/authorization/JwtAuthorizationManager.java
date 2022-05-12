package com.yj2025.gateway.security.authorization;

import com.yj2025.gateway.PathMatcherAuthoritiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

/**
 * jwt 鉴权管理器，用于判断是否有资源的访问权限
 */
@Slf4j
public class JwtAuthorizationManager extends AbstracAuthorizationManager {


    public JwtAuthorizationManager(PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader) {
        super(pathMatcherAuthoritiesLoader);
    }

    @Override
    protected Mono<AuthorizationDecision> checkAuthorities(Mono<Authentication> authenticationMono, Set<String> pathAuthorities) {
        return
                authenticationMono.cast(JwtAuthenticationToken.class)
                        .flatMapIterable(authentication -> ((Jwt) authentication.getCredentials()).getClaim("authorities") != null ? ((Jwt) authentication.getCredentials()).getClaim("authorities") : new HashSet<>())
                        .any(authority -> pathAuthorities.contains(authority))
                        .map(AuthorizationDecision::new)
                        .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
