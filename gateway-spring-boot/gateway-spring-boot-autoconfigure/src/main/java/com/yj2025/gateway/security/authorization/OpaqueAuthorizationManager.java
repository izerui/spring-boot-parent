package com.yj2025.gateway.security.authorization;

import com.yj2025.gateway.PathMatcherAuthoritiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

/**
 * opaque 鉴权管理器，用于判断是否有资源的访问权限
 */
@Slf4j
public class OpaqueAuthorizationManager extends AbstracAuthorizationManager {


    public OpaqueAuthorizationManager(PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader) {
        super(pathMatcherAuthoritiesLoader);
    }

    @Override
    protected Mono<AuthorizationDecision> checkAuthorities(Mono<Authentication> authenticationMono, Set<String> pathAuthorities) {
        // 验证用户authorities里面是否具备该权限
        return
                authenticationMono.cast(BearerTokenAuthentication.class)
                        .flatMapIterable(authentication -> authentication.getAuthorities() != null ? authentication.getAuthorities() : new HashSet<>())
                        .any(authority -> pathAuthorities.contains(authority))
                        .map(AuthorizationDecision::new)
                        .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
