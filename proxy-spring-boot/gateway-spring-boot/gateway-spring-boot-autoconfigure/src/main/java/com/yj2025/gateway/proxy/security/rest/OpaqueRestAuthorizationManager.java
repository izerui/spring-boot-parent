package com.yj2025.gateway.proxy.security.rest;

import com.yj2025.gateway.proxy.PathMatcherAuthoritiesLoader;
import com.yj2025.gateway.proxy.security.AbstracAuthorizationManager;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * opaque 鉴权管理器，用于判断是否有资源的访问权限
 */
@Slf4j
public class OpaqueRestAuthorizationManager extends AbstracAuthorizationManager {


    public OpaqueRestAuthorizationManager(PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader) {
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
        // 验证用户authorities里面是否具备该权限
        return
                authenticationMono.cast(BearerTokenAuthentication.class)
                        .flatMapIterable(authentication -> Optional.ofNullable((JSONArray)authentication.getTokenAttributes().get("authorities")).orElse(new JSONArray()))
                        .any(authority -> pathAuthorities.contains(authority))
                        .map(AuthorizationDecision::new)
                        .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
