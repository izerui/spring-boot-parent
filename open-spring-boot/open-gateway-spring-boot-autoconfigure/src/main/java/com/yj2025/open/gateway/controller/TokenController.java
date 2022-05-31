package com.yj2025.open.gateway.controller;

import com.yj2025.open.commons.Constants;
import com.yj2025.open.commons.RespVo;
import com.yj2025.open.gateway.GatewayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author liuyuhua
 */
@Configuration
@RestController
public class TokenController {

    /**
     * https://docs.spring.io/spring-security/site/docs/5.1.1.RELEASE/reference/html/webclient.html
     **/
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private GatewayProperties properties;

    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
    public Mono<RespVo> getToken(@RequestParam(Constants.CLIENT_ID_FIELDNAME) String clientId,
                                 @RequestParam(Constants.CLIENT_SECRET_FIELDNAME) String clientSecret,
                                 @RequestParam(Constants.GRANT_TYPE_FIELDNAME) String grantType) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.set(Constants.CLIENT_ID_FIELDNAME, clientId);
        paramMap.set(Constants.CLIENT_SECRET_FIELDNAME, clientSecret);
        paramMap.set(Constants.GRANT_TYPE_FIELDNAME, grantType);
        return webClientBuilder.build()
                .post()
                .uri("http://" + properties.getOauthApp() + "/oauth/token")
                .body(BodyInserters.fromFormData(paramMap))
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .retrieve()
                .bodyToMono(Map.class)
                .map(map -> RespVo.success(map))
                .onErrorReturn(RespVo.error("oauth_error", "验证失败"));
    }

}
