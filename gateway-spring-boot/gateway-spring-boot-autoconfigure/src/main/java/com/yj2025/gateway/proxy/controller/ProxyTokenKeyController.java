package com.yj2025.gateway.proxy.controller;

import com.yj2025.gateway.proxy.GatewayProxyProperties;
import com.yj2025.gateway.proxy.request.RefreshRequest;
import com.yj2025.gateway.proxy.request.TokenRequest;
import com.yj2025.oauth2.security.support.MappingUrls;
import com.yj2025.oauth2.security.support.RespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@RestController
public class ProxyTokenKeyController {

    /**
     * https://docs.spring.io/spring-security/site/docs/5.1.1.RELEASE/reference/html/webclient.html
     **/
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ServerBearerTokenAuthenticationConverter tokenAuthenticationConverter;

    @Autowired
    private GatewayProxyProperties properties;

    @PostMapping(MappingUrls.OAUTH_TOKEN_URL)
    public Mono<String> getToken(@RequestBody TokenRequest request) {
        GatewayProxyProperties.Oauth2Properties oauth2Properties = properties.getOauth2();
        MultiValueMap<String, String> multiValueMap = request.newRequest(
                oauth2Properties.getClientId(),
                oauth2Properties.getClientSecret(),
                "password"
        );
        return webClientBuilder
                .build()
                .post()
                .uri("http://" + oauth2Properties.getAppName() + MappingUrls.OAUTH_TOKEN_URL)
                .body(BodyInserters.fromFormData(multiValueMap))
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(throwable -> Mono.just(RespVo.error("error", throwable.getMessage()).toJson()));
    }

    @PostMapping(MappingUrls.OAUTH_REFRESH_URL)
    public Mono<String> refreshToken(@RequestBody RefreshRequest request) {
        GatewayProxyProperties.Oauth2Properties oauth2Properties = properties.getOauth2();
        MultiValueMap<String, String> multiValueMap = request.newRequest(
                oauth2Properties.getClientId(),
                oauth2Properties.getClientSecret(),
                "refresh_token"
        );
        return webClientBuilder
                .build()
                .post()
                .uri("http://" + oauth2Properties.getAppName() + MappingUrls.OAUTH_TOKEN_URL)
                .body(BodyInserters.fromFormData(multiValueMap))
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(throwable -> Mono.just(RespVo.error("error", throwable.getMessage()).toJson()));
    }


    @GetMapping(MappingUrls.OAUTH_REVOKE_URL)
    public Mono<String> revokeToken(ServerWebExchange exchange) {
        return tokenAuthenticationConverter.convert(exchange)
                .map(authentication -> {
                    BearerTokenAuthenticationToken bearerTokenAuthenticationToken = (BearerTokenAuthenticationToken) authentication;
                    return bearerTokenAuthenticationToken.getToken();
                }).flatMap(accessToken -> webClientBuilder
                        .build()
                        .get()
                        .uri("http://" + properties.getOauth2().getAppName() + MappingUrls.OAUTH_REVOKE_URL + "?access_token=" + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .retrieve()
                        .bodyToMono(String.class)
                        .onErrorResume(throwable -> Mono.just(RespVo.error("error", throwable.getMessage()).toJson())));
    }

    @GetMapping(MappingUrls.TOKEN_KEY_URL)
    public Mono<String> getKey() {
        return webClientBuilder
                .build()
                .get()
                .uri("http://" + properties.getOauth2().getAppName() + MappingUrls.TOKEN_KEY_URL)
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(throwable -> Mono.just(RespVo.error("error", throwable.getMessage()).toJson()));
    }
}
