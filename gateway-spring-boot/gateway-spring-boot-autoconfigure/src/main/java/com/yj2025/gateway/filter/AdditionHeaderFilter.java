package com.yj2025.gateway.filter;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
        final ServerWebExchange finalExchange = exchange;
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .flatMap(bearerTokenAuthentication -> {
                    if (bearerTokenAuthentication == null) {
                        return chain.filter(finalExchange);
                    }
                    Map<String, Object> claims = ((BearerTokenAuthentication) bearerTokenAuthentication).getTokenAttributes();
                    ServerHttpRequest.Builder requestBuilder = finalExchange.getRequest().mutate();
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
                    return chain.filter(finalExchange.mutate().request(requestBuilder.build()).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
