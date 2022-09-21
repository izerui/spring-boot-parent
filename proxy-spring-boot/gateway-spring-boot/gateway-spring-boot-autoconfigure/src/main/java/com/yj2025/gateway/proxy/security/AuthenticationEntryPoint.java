package com.yj2025.gateway.proxy.security;

import com.yj2025.gateway.proxy.utils.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.yj2025.gateway.proxy.utils.ResponseUtils.writeResponse;

/**
 * 没有登录或token过期时处理结果
 */
public class AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        return ResponseUtils.writeResponse(exchange, false, null, "unlogin", "用户未登录");
    }
}
