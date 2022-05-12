package com.yj2025.gateway.security.authorization;

import com.yj2025.gateway.PathMatcherAuthoritiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * opaque 鉴权管理器，用于判断是否有资源的访问权限
 */
@Slf4j
abstract class AbstracAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader;

    private volatile Map<String, Set<String>> pathMatcherAuthoritiesMap = new HashMap<>();
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public AbstracAuthorizationManager(PathMatcherAuthoritiesLoader pathMatcherAuthoritiesLoader) {
        this.pathMatcherAuthoritiesLoader = pathMatcherAuthoritiesLoader;
        this.initGlobAuthorizations();
    }

    /**
     * 初始化全局权限对应关系,后续考虑动态读取redis中的值
     */
    public void initGlobAuthorizations() {
        this.pathMatcherAuthoritiesMap = pathMatcherAuthoritiesLoader.getPathMatcherAuthoritiesMap();
        if (this.pathMatcherAuthoritiesMap == null) {
            this.pathMatcherAuthoritiesMap = new HashMap<>();
        }
        log.info("加载全局URL权限拦截");
        this.pathMatcherAuthoritiesMap.forEach((pathMatcher, authorities) -> {
            log.info("{} : {}", pathMatcher, authorities.toString());
        });
    }

    protected Map<String, Set<String>> getPathMatcherAuthoritiesMap() {
        return this.pathMatcherAuthoritiesMap;
    }

    @Override
    public final Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        String path = authorizationContext.getExchange().getRequest().getURI().getPath();
        HttpMethod method = authorizationContext.getExchange().getRequest().getMethod();
        Set<String> pathAuthorities = new HashSet<>();
        Set<String> keySet = this.getPathMatcherAuthoritiesMap().keySet();
        for (String pathMatcher : keySet) {
            // example: GET:/device/**  POST:/user/
            if (pathMatcher.contains(":/")) {
                String[] split = pathMatcher.split(":");
                String httpMethod = split[0];
                String url = split[1];
                if (antPathMatcher.match(url, path)) {
                    if (method.matches(httpMethod) || "ALL".equalsIgnoreCase(httpMethod)) {
                        pathAuthorities.addAll(this.getPathMatcherAuthoritiesMap().get(pathMatcher));
                    }
                }
            } else if (antPathMatcher.match(pathMatcher, path)) {
                pathAuthorities.addAll(this.getPathMatcherAuthoritiesMap().get(pathMatcher));
            }
            log.debug("auths: {}", pathAuthorities);
        }
        Mono<Authentication> filter = mono.filter(Authentication::isAuthenticated);
        if (pathAuthorities.isEmpty()) {  // 如果没有指定拦截则默认放行
            return filter.map(bearerTokenAuthentication -> new AuthorizationDecision(true));
        } else {
            return checkAuthorities(filter, pathAuthorities);
        }
    }

    protected abstract Mono<AuthorizationDecision> checkAuthorities(Mono<Authentication> authenticationMono, Set<String> pathAuthorities);

}
