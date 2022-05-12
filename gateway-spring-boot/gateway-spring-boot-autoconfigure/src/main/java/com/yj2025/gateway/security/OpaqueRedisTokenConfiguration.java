package com.yj2025.gateway.security;

import com.yj2025.oauth2.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.CLIENT_ID;
import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.EXPIRES_AT;

/**
 * redis token配置，通过redis获取token信息，进行验证
 */
@Configuration
@ConditionalOnProperty(name = "gateway.oauth2.auth-type", matchIfMissing = true, havingValue = "OPAQUE_REDIS")
public class OpaqueRedisTokenConfiguration implements Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec> {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisTokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Override
    public void customize(ServerHttpSecurity.OAuth2ResourceServerSpec auth) {
        auth.opaqueToken()
                .introspector(new RedisTokenIntrospector(redisTokenStore()));
    }

    private class RedisTokenIntrospector implements ReactiveOpaqueTokenIntrospector {

        private RedisTokenStore redisTokenStore;

        public RedisTokenIntrospector(RedisTokenStore redisTokenStore) {
            this.redisTokenStore = redisTokenStore;
        }

        @Override
        public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
            OAuth2AccessToken oAuth2AccessToken = redisTokenStore.readAccessToken(token);
            OAuth2Authentication authentication = redisTokenStore.readAuthentication(token);
            if (oAuth2AccessToken == null || authentication == null) {
                throw new BadCredentialsException("用户未登录!");
            }
            Map<String, Object> claims = new HashMap<>();
            if (authentication.getOAuth2Request().getClientId() != null) {
                claims.put(CLIENT_ID, authentication.getOAuth2Request().getClientId());
            }
            if (oAuth2AccessToken.getExpiration().toInstant() != null) {
                claims.put(EXPIRES_AT, oAuth2AccessToken.getExpiration().toInstant());
            }
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                claims.put("userCode", user.getUserCode());
                claims.put("userName", user.getUserName());
                claims.put("entCode", user.getEntCode());
                claims.put("entName", user.getEntName());
                claims.put("accountCode", user.getAccountCode());
                claims.put("accountName", user.getAccountName());
            }
            return Mono.just(new DefaultOAuth2AuthenticatedPrincipal(
                    authentication.getName(),
                    claims,
                    authentication.getAuthorities()));
        }
    }

}