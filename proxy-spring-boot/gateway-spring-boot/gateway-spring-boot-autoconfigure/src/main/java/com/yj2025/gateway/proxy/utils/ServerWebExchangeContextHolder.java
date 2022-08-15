package com.yj2025.gateway.proxy.utils;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * orignal: ReactiveRequestContextHolder
 *
 * @author L.cm
 */
public class ServerWebExchangeContextHolder {
    public static final Class<ServerWebExchange> CONTEXT_KEY = ServerWebExchange.class;

    /**
     * Gets the {@code Mono<ServerWebExchange>} from Reactor {@link reactor.util.context.Context}
     *
     * @return the {@code Mono<ServerWebExchange>}
     */
    public static Mono<ServerWebExchange> getExchange() {
        return Mono.subscriberContext()
                .map(ctx -> ctx.get(CONTEXT_KEY));
    }

}
