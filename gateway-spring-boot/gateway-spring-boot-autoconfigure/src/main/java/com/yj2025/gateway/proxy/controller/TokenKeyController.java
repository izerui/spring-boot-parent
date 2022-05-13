package com.yj2025.gateway.proxy.controller;

import com.yj2025.gateway.proxy.request.RefreshRequest;
import com.yj2025.gateway.proxy.request.TokenRequest;
import com.yj2025.gateway.proxy.GatewayProxyProperties;
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
public class TokenKeyController {

    /**
     * https://docs.spring.io/spring-security/site/docs/5.1.1.RELEASE/reference/html/webclient.html
     **/
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ServerBearerTokenAuthenticationConverter tokenAuthenticationConverter;

    @Autowired
    private GatewayProxyProperties properties;

    @PostMapping("/oauth/token")
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
                .uri("http://" + oauth2Properties.getAppName() + "/oauth/token")
                .body(BodyInserters.fromFormData(multiValueMap))
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .retrieve()
                .bodyToMono(String.class);
    }

    @PostMapping("/oauth/refresh")
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
                .uri("http://" + oauth2Properties.getAppName() + "/oauth/token")
                .body(BodyInserters.fromFormData(multiValueMap))
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .retrieve()
                .bodyToMono(String.class);
    }


    @GetMapping("/oauth/revoke")
    public Mono<String> revokeToken(ServerWebExchange exchange) {
        GatewayProxyProperties.Oauth2Properties oauth2Properties = properties.getOauth2();
        return tokenAuthenticationConverter.convert(exchange)
                .map(authentication -> {
                    BearerTokenAuthenticationToken bearerTokenAuthenticationToken = (BearerTokenAuthenticationToken) authentication;
                    return bearerTokenAuthenticationToken.getToken();
                }).flatMap(accessToken -> webClientBuilder
                        .build()
                        .get()
                        .uri("http://" + oauth2Properties.getAppName() + "/oauth/revoke?access_token=" + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .retrieve()
                        .bodyToMono(String.class));
    }

    @GetMapping("/oauth/token_key")
    public Mono<String> getKey() {
        GatewayProxyProperties.Oauth2Properties oauth2Properties = properties.getOauth2();
        return webClientBuilder
                .build()
                .get()
                .uri("http://" + oauth2Properties.getAppName() + "/oauth/token_key")
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .retrieve()
                .bodyToMono(String.class);
    }
}
