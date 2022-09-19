package com.yj2025.gateway.proxy.utils;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * orignal: ReactiveRequestContextHolder
 *
 * @author L.cm
 */
public class ServerWebExchangeContextHolder implements WebFilter {
    public static final Class<ServerWebExchange> CONTEXT_KEY = ServerWebExchange.class;

    /**
     * Gets the {@code Mono<ServerWebExchange>} from Reactor {@link reactor.util.context.Context}
     *
     * @return the {@code Mono<ServerWebExchange>}
     */
    public static Mono<ServerWebExchange> getExchange() {
        return Mono.subscriberContext()
                .map(ctx -> ctx.get(CONTEXT_KEY))
                .onErrorReturn(null);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange).subscriberContext(ctx -> ctx.put(CONTEXT_KEY, exchange));
    }
}
