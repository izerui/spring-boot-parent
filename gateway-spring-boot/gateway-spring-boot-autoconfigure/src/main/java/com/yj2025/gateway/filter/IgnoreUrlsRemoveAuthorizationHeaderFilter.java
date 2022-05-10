package com.yj2025.gateway.filter;

import com.yj2025.gateway.GatewayProxyProperties;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * 白名单路径访问时需要移除Authorization请求头
 * Created by macro on 2020/7/24.
 */
public class IgnoreUrlsRemoveAuthorizationHeaderFilter implements WebFilter {

    private GatewayProxyProperties gatewayProxyProperties;

    public IgnoreUrlsRemoveAuthorizationHeaderFilter(GatewayProxyProperties proxyProperties) {
        this.gatewayProxyProperties = proxyProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        PathMatcher pathMatcher = new AntPathMatcher();
        //白名单路径移除Authorization请求头
        for (String ignoreUrl : gatewayProxyProperties.getIgnoredUrls()) {
            if (pathMatcher.match(ignoreUrl, uri.getPath())) {
                request = exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove("Authorization")).build();
                exchange = exchange.mutate().request(request).build();
                return chain.filter(exchange);
            }
        }
        return chain.filter(exchange);
    }
}
