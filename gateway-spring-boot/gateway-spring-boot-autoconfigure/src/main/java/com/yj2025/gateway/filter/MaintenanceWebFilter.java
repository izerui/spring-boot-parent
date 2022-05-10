package com.yj2025.gateway.filter;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MaintenanceWebFilter implements WebFilter {

    private Boolean maintenance;
    private Gson gson;

    public MaintenanceWebFilter(Boolean maintenance, Gson gson) {
        this.maintenance = maintenance;
        this.gson = gson;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (maintenance != null && maintenance) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("data", null);
            map.put("errCode", "maintenance");
            map.put("errMsg", "系统升级维护中,请稍后再试...");
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            response.getHeaders().set("characterEncoding", "UTF-8");
            return response.writeWith(Flux.just(response.bufferFactory().wrap(gson.toJson(map).getBytes(StandardCharsets.UTF_8))));
        }
        return chain.filter(exchange);
    }
}
