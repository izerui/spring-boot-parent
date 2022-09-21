package com.yj2025.gateway.proxy.filter;

import com.yj2025.gateway.proxy.GatewayProxyProperties;
import com.yj2025.gateway.proxy.utils.NetworkUtils;
import com.yj2025.gateway.proxy.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
public class MaintenanceWebFilter implements WebFilter {

    private GatewayProxyProperties properties;

    public MaintenanceWebFilter(GatewayProxyProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (properties.isMaintenance()) {
            if (properties.getWhitelistIp() != null) {
                String clientIp = NetworkUtils.getIpAddress(exchange);
                boolean inWhitelist = Arrays.stream(properties.getWhitelistIp().split(",")).anyMatch(s -> clientIp.equals(s));
                if (inWhitelist) {
                    log.info("维护模式 request-ip: {} 放行...", clientIp);
                    return chain.filter(exchange);
                }
                log.info("维护模式 request-ip: {} 拦截...", clientIp);
            }
            return ResponseUtils.writeResponse(exchange, false, null, "maintenance", "系统升级维护中,请稍后再试...");
        }
        return chain.filter(exchange);
    }
}
