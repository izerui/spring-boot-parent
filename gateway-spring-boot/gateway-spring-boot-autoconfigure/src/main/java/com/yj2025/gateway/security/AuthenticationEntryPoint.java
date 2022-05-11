package com.yj2025.gateway.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.yj2025.gateway.utils.ResponseUtils.writeResponse;

/**
 * 没有登录或token过期时处理结果
 */
public class AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        return writeResponse(exchange, false, null, "unlogin", "用户未登录");
    }
}
