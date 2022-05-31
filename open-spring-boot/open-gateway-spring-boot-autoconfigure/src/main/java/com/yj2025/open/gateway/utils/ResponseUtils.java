package com.yj2025.open.gateway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ResponseUtils {

    private final static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static Mono<Void> writeResponse(ServerWebExchange exchange, boolean success, Object data, String errCode, String errMsg) {
        return writeResponse(exchange, success, data, errCode, errMsg, null);
    }

    public static Mono<Void> writeResponse(ServerWebExchange exchange, boolean success, Object data, String errCode, String errMsg, Consumer<ServerHttpResponse> responseConsumer) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("data", data);
        map.put("errCode", errCode);
        map.put("errMsg", errMsg);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().set("characterEncoding", "UTF-8");
        if (responseConsumer != null) {
            responseConsumer.accept(response);
        }
        byte[] bytes = null;
        try {
            bytes = OBJECT_MAPPER.writeValueAsBytes(map);
        } catch (JsonProcessingException e) {
            ReflectionUtils.handleReflectionException(e);
        }
        return response.writeWith(Flux.just(response.bufferFactory().wrap(bytes)));
    }
}
