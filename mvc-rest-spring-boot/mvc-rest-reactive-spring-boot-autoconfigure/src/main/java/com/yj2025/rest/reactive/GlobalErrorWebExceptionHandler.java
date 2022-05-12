package com.yj2025.rest.reactive;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.*;
import static org.springframework.boot.web.error.ErrorAttributeOptions.of;

public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler implements Constants {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, applicationContext);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {
        final Map<String, Object> errorPropertiesMap = getErrorAttributes(request, of(EXCEPTION, STACK_TRACE, MESSAGE, BINDING_ERRORS));
        //feign 请求
        if (errorPropertiesMap.containsKey(EXCEPTION_SERIALIZABLE)) {
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(BodyInserters.fromValue(errorPropertiesMap));
        }
        Integer status = (Integer) errorPropertiesMap.get("status");
        //非 feign 请求 统一返回status 200
        String clientType = request.headers().firstHeader(CLIENT_TYPE);
        if (clientType == null || !FEIGN_REQUEST_TYPE.equals(clientType)) {
            status = HttpStatus.OK.value();
        }
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }
}
