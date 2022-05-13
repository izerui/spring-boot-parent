package com.yj2025.gateway.security.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * redis token配置，通过redis获取token信息，进行验证(效率其次)
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


}