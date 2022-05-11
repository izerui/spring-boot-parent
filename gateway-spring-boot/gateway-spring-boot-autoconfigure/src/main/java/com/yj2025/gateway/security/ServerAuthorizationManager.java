package com.yj2025.gateway.security;

import com.yj2025.gateway.PathMatcherAuthoritiesRemoteLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 鉴权管理器，用于判断是否有资源的访问权限
 */
@Slf4j
@Component
public class ServerAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Autowired
    private PathMatcherAuthoritiesRemoteLoader pathMatcherAuthoritiesLoader;
    private volatile Map<String, Set<String>> pathMatcherAuthoritiesMap = new HashMap<>();
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 初始化全局权限对应关系,后续考虑动态读取redis中的值
     */
    @PostConstruct
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

    public Map<String, Set<String>> getPathMatcherAuthoritiesMap() {
        return this.pathMatcherAuthoritiesMap;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        //从Redis中获取当前路径可访问角色列表
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
            } else if(antPathMatcher.match(pathMatcher, path)){
                pathAuthorities.addAll(this.getPathMatcherAuthoritiesMap().get(pathMatcher));
            }
            log.debug("auths: {}", pathAuthorities);
        }
        Mono<BearerTokenAuthentication> filter = mono.cast(BearerTokenAuthentication.class)
                .filter(AbstractAuthenticationToken::isAuthenticated);
        if (pathAuthorities.isEmpty()) {  // 如果没有指定拦截则默认放行
            return filter.map(bearerTokenAuthentication -> new AuthorizationDecision(true));
        } else { // 否则验证用户authorities里面是否具备该权限
            return
                    filter
                            .flatMapIterable(authentication -> authentication.getAuthorities() != null ? authentication.getAuthorities() : new HashSet<>())
                            .any(authority -> pathAuthorities.contains(authority))
                            .map(AuthorizationDecision::new)
                            .defaultIfEmpty(new AuthorizationDecision(false));
        }
    }

}
