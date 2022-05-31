package com.yj2025.open.gateway.filter;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.yj2025.open.commons.ClientStore;
import com.yj2025.open.gateway.utils.ExchangeUtils;
import com.yj2025.open.gateway.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.yj2025.open.commons.Constants.*;

/**
 * 当传入的Authorization校验通过后执行，或者未传入Authorization也会执行当前过滤器
 *
 * @author liuyuhua
 */
@Slf4j
public class HeaderFilter implements WebFilter {

    private final static String OAUTH_TOKEN_PATH = "/oauth/token";

    private ClientStore clientStore;

    public HeaderFilter(ClientStore clientStore) {
        this.clientStore = clientStore;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        try {
            // 未传Authorization： /oauth/token 白名单
            if (OAUTH_TOKEN_PATH.equalsIgnoreCase(exchange.getRequest().getPath().value())) {
                return chain.filter(exchange);
            }
            // 传入的身份信息
            String authHeader = exchange.getRequest().getHeaders().getFirst(HEADER_AUTHORIZATION_FIELDNAME);
            // 传入的时间戳
            String unixTimestamp = exchange.getRequest().getHeaders().getFirst(HEADER_UNIX_TIMESTAMP_FIELDNAME);
            Assert.notNull(unixTimestamp, "缺少时间戳");
            // 传入的签名信息
            String sign = exchange.getRequest().getHeaders().getFirst(HEADER_SIGN_FIELDNAME);
            Assert.notNull(sign, "缺少签名信息");
            // 未传Authorization 则抛出异常
            if (authHeader == null) {
                return ExchangeUtils.wirteErrorResponse(exchange, "AUTHORIZATION_ERROR", "缺少Authorization身份信息!");
            }
            String tokenValue = authHeader.replaceFirst("Bearer ", "").trim();
            JWT jwt = JWTParser.parse(tokenValue);
            Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();
            String clientId = String.valueOf(claims.get(CLIENT_ID_FIELDNAME));
            String tenantId = String.valueOf(claims.get(TENANT_ID_FIELDNAME));
            // 最后一次生成token时，保存的签名密钥
            String clientSecret = clientStore.getClientSecret(clientId);
            // 验签
            boolean verify = SignUtils.verify(tokenValue, Long.valueOf(unixTimestamp), sign, clientSecret);
            if (!verify) {
                return ExchangeUtils.wirteErrorResponse(exchange, "SIGN_ERROR", "Sign签名验证失败!");
            }
            // 增强header信息
            ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();
            requestBuilder.header(CLIENT_ID_FIELDNAME, clientId);
            requestBuilder.header(TENANT_ID_FIELDNAME, tenantId);
            exchange = exchange.mutate().request(requestBuilder.build()).build();
        } catch (Exception ex) {
            return ExchangeUtils.wirteErrorResponse(exchange, "OAUTH_ERROR", ex.getMessage());
        }
        return chain.filter(exchange);
    }
}
