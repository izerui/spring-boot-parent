package com.yj2025.open.gateway.filter;

import com.yj2025.open.commons.ClientStore;
import com.yj2025.open.gateway.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.yj2025.open.commons.Constants.*;

/**
 * 根据token解析出用户等信息,放入header中,header补充过滤器
 */
@Slf4j
public class CheckSignatureFilter implements WebFilter {

    private ClientStore clientStore;

    public CheckSignatureFilter(ClientStore clientStore) {
        this.clientStore = clientStore;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> {
                    if (authentication == null) {
                        return exchange;
                    }
                    String accessToken = authentication.getToken().getTokenValue();
                    // 传入的时间戳
                    String unixTimestamp = exchange.getRequest().getHeaders().getFirst(HEADER_UNIX_TIMESTAMP_FIELDNAME);
                    Assert.notNull(unixTimestamp, "缺少时间戳");
                    // 传入的签名信息
                    String sign = exchange.getRequest().getHeaders().getFirst(HEADER_SIGN_FIELDNAME);
                    Assert.notNull(sign, "缺少签名信息");

                    Map<String, Object> claims = authentication.getToken().getClaims();
                    String clientId = String.valueOf(claims.get(CLIENT_ID_FIELDNAME));
                    String tenantId = String.valueOf(claims.get(TENANT_ID_FIELDNAME));

                    // 最后一次生成token时，保存的签名密钥
                    String clientSecret = clientStore.getClientSecret(clientId);

                    // 验签
                    boolean verify = SignUtils.verify(accessToken, Long.valueOf(unixTimestamp), sign, clientSecret);
                    if (!verify) {
                        throw new RuntimeException("Sign签名验证失败");
                    }

                    // 增强header信息
                    ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();
                    requestBuilder.header(CLIENT_ID_FIELDNAME, clientId);
                    requestBuilder.header(TENANT_ID_FIELDNAME, tenantId);
                    return exchange.mutate().request(requestBuilder.build()).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(wrapExchange -> chain.filter(wrapExchange));
    }
}
