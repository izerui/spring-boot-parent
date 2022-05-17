package com.yj2025.gateway.proxy.filter;

import com.yj2025.gateway.proxy.GatewayProxyProperties;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 白名单路径移除附带的用户身份请求信息
 */
public class RemoveAuthorizationFilter implements WebFilter {

    private GatewayProxyProperties gatewayProxyProperties;

    public RemoveAuthorizationFilter(GatewayProxyProperties proxyProperties) {
        this.gatewayProxyProperties = proxyProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        PathMatcher pathMatcher = new AntPathMatcher();
        //白名单路径移除附带的用户身份请求信息
        for (String ignoreUrl : gatewayProxyProperties.getIgnoredUrls()) {
            if (pathMatcher.match(ignoreUrl, uri.getPath())) {
                request = exchange.getRequest().mutate()
                        .headers(httpHeaders -> httpHeaders.remove("Authorization"))
                        .uri(URI.create(removeParam(uri.toString(),"access_token")))
                        .build();
                exchange = exchange.mutate().request(request).build();
                return chain.filter(exchange);
            }
        }
        return chain.filter(exchange);
    }

    /**
     * 去除url指定参数
     * @param url
     * @param name
     * @return
     */
    public static String removeParam(String url, String ...name){
        for (String s : name) {
            // 使用replaceAll正则替换,replace不支持正则
            url = url.replaceAll("&?"+s+"=[^&]*","");
        }
        return url;
    }
}
