package com.yj2025.gateway.proxy.controller;

import com.yj2025.gateway.proxy.GatewayProxyProperties;
import com.yj2025.oauth2.security.support.MappingUrls;
import com.yj2025.oauth2.security.support.QrcodeConstants;
import com.yj2025.oauth2.security.support.RespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * 二维码登录相关接口
 * Created by serv on 2016/12/20.
 */
@Configuration
@RestController
public class ProxyQrcodeController {

    @Autowired
    private GatewayProxyProperties properties;
    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping(MappingUrls.QRCODE_REDIRECT_URL)
    public void redirect(ServerHttpResponse response) {
        //判断user Agent 做相应的跳转
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create("http://www.yunji2025.com"));
    }


    @PostMapping(MappingUrls.QRCODE_GENERATE_URL)
    public Mono<String> generateQrCode(ServerWebExchange exchange) {
        return webClientBuilder
                .build()
                .post()
                .uri("http://" + properties.getOauth2().getAppName() + MappingUrls.QRCODE_GENERATE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .exchange()
                .flatMap(clientResponse -> {
                    // 将服务返回的cookie设置到response中
                    ServerHttpResponse response = exchange.getResponse();
                    MultiValueMap<String, ResponseCookie> myCookies = response.getCookies();
                    myCookies.addAll(clientResponse.cookies());
                    return clientResponse.bodyToMono(String.class);
                })
                .onErrorResume(throwable -> Mono.just(RespVo.error("error", throwable.getMessage()).toJson()));
    }


    @ResponseBody
    @PostMapping(MappingUrls.QRCODE_VALIDATE_URL)
    public Mono<String> validateQrCode(ServerWebExchange exchange) {
        // 读取客户端的cookie设置到request中
        HttpCookie ticketKeyCookie = exchange.getRequest().getCookies().getFirst(QrcodeConstants.QRCODE_TICKET_KEY);
        return webClientBuilder
                .build()
                .post()
                .uri("http://" + properties.getOauth2().getAppName() + MappingUrls.QRCODE_VALIDATE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .cookies(requestCookies -> {
                    if (ticketKeyCookie != null) {
                        requestCookies.add(ticketKeyCookie.getName(), ticketKeyCookie.getValue());
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(throwable -> Mono.just(RespVo.error("error", throwable.getMessage()).toJson()));
    }


}
