package com.yj2025.gateway.security.jwt;

import com.yj2025.gateway.PathMatcherAuthoritiesLoader;
import com.yj2025.gateway.security.AbstracAuthorizationManager;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

/**
 * jwt 鉴权管理器，用于判断是否有资源的访问权限
 */
@Slf4j
public class JwtAuthorizationManager extends AbstracAuthorizationManager {


    public JwtAuthorizationManager(PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader) {
        super(pathMatcherAuthoritiesLoader);
    }

    /**
     * authorities 设置的地方： {@link org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter#convertAccessToken}
     * @param authenticationMono
     * @param pathAuthorities
     * @return
     */
    @Override
    protected Mono<AuthorizationDecision> checkAuthorities(Mono<Authentication> authenticationMono, Set<String> pathAuthorities) {
        return
                authenticationMono.cast(JwtAuthenticationToken.class)
                        .flatMapIterable(authentication -> Optional.ofNullable((JSONArray)authentication.getToken().getClaim("authorities")).orElse(new JSONArray()))
                        .any(authority -> pathAuthorities.contains(authority))
                        .map(AuthorizationDecision::new)
                        .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
