package com.yj2025.gateway.proxy.filter;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 根据token解析出用户等信息,放入header中,header补充过滤器
 */
@Slf4j
public class AdditionHeaderFilter implements WebFilter {

    private final static Set<String> includeHeaders = Sets.newHashSet(
            "accountCode",
            "accountName",
            "entCode",
            "entName",
            "userCode",
            "userName",
            "client_id"
    );

    private final static Set<String> encodeHeaders = Sets.newHashSet("accountName", "entName", "userName");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // https://qa.icopy.site/questions/61397201/how-do-you-set-programmatically-the-authentication-object-in-spring-security-rea
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication == null) {
                        return exchange;
                    }
                    Map<String, Object> claims = new HashMap<>();
                    if(authentication instanceof JwtAuthenticationToken) {
                        claims = ((JwtAuthenticationToken) authentication).getToken().getClaims();
                    }else if(authentication instanceof BearerTokenAuthentication) {
                        claims = ((BearerTokenAuthentication) authentication).getTokenAttributes();
                    }
                    ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();
                    claims.forEach((key, value) -> {
                        if (includeHeaders.contains(key)) {
                            // 需要编码的请求头
                            if (encodeHeaders.contains(key)) {
                                try {
                                    requestBuilder.header(key, URLEncoder.encode(String.valueOf(value), "UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                    // do nothing
                                }
                            } else {
                                requestBuilder.header(key, String.valueOf(value));
                            }
                        }
                    });
                    return exchange.mutate().request(requestBuilder.build()).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(wrapExchange -> chain.filter(wrapExchange));
    }
}
