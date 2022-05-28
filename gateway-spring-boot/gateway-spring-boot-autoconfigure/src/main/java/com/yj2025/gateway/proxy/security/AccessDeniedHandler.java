package com.yj2025.gateway.proxy.security;

import com.yj2025.gateway.proxy.utils.ResponseUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.yj2025.gateway.proxy.utils.ResponseUtils.writeResponse;

/**
 * 用户未授权handler
 */
public class AccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return ResponseUtils.writeResponse(exchange, false, denied.getMessage(), "ACCESS_DENIED", "没有权限");
    }
}
