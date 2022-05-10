package com.yj2025.gateway.controller;

import com.yj2025.gateway.GatewayProxyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
@RestController
public class TokenController {

    /**
     * https://docs.spring.io/spring-security/site/docs/5.1.1.RELEASE/reference/html/webclient.html
     **/
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ServerBearerTokenAuthenticationConverter tokenAuthenticationConverter;

    @Autowired
    private GatewayProxyProperties gatewayProxyProperties;

    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
    public Mono<Map> getToken(@RequestParam("username") String username,
                              @RequestParam("password") String password) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.set("client_id", gatewayProxyProperties.getClient_id());
        paramMap.set("client_secret", gatewayProxyProperties.getClient_secret());
        paramMap.set("grant_type", "password");
        paramMap.set("username", username);
        paramMap.set("password", password);
        return webClientBuilder
                .build()
                .post()
                .uri("http://" + gatewayProxyProperties.getAuthAppName() + "/oauth/token")
                .body(BodyInserters.fromFormData(paramMap))
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .retrieve()
                .bodyToMono(Map.class);
    }

    @RequestMapping(value = "/oauth/refresh", method = RequestMethod.POST)
    public Mono<String> refreshToken(@RequestHeader("userCode") String userCode,
                                     @RequestParam("refresh_token") String refreshToken) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.set("client_id", gatewayProxyProperties.getClient_id());
        paramMap.set("client_secret", gatewayProxyProperties.getClient_secret());
        paramMap.set("grant_type", "refresh_token");
        paramMap.set("refresh_token", refreshToken);
        return webClientBuilder
                .build()
                .post()
                .uri("http://" + gatewayProxyProperties.getAuthAppName() + "/oauth/token")
                .body(BodyInserters.fromFormData(paramMap))
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("userCode", userCode)
                .retrieve()
                .bodyToMono(String.class);
    }


    @RequestMapping(value = "/oauth/revoke", method = RequestMethod.POST)
    public Mono<String> revokeToken(ServerWebExchange exchange) {
        return tokenAuthenticationConverter.convert(exchange)
                .map(authentication -> {
                    BearerTokenAuthenticationToken bearerTokenAuthenticationToken = (BearerTokenAuthenticationToken) authentication;
                    return bearerTokenAuthenticationToken.getToken();
                }).flatMap(accessToken -> webClientBuilder
                        .build()
                        .get()
                        .uri("http://" + gatewayProxyProperties.getAuthAppName() + "/oauth/revoke")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .retrieve()
                        .bodyToMono(String.class));
    }
}
