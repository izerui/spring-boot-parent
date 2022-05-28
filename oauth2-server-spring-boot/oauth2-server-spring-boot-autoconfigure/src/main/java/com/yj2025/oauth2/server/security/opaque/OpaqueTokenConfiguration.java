package com.yj2025.oauth2.server.security.opaque;


import com.yj2025.oauth2.server.security.ExpandEndpointsConfigurer;
import com.yj2025.oauth2.server.security.TokenInfoEnhancer;
import com.yj2025.oauth2.server.security.TokenSerivces;
import com.yj2025.oauth2.server.security.UserDetailsServiceAdapter;
import com.yj2025.oauth2.server.security.provider.RefreshAuthServiceWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Arrays;


@Configuration
@ConditionalOnProperty(name = "oauth2.server.jwt.enabled", matchIfMissing = true, havingValue = "false")
public class OpaqueTokenConfiguration implements ExpandEndpointsConfigurer {

    @Autowired
    private RedisTokenStore redisTokenStore;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private UserDetailsServiceAdapter userDetailsServiceAdapter;

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new TokenInfoEnhancer();
    }

    @Bean
    public TokenSerivces tokenSerivces() {
        TokenSerivces tokenSerivces = new TokenSerivces() {{
            this.setTokenStore(redisTokenStore);
            this.setSupportRefreshToken(true);
            this.setReuseRefreshToken(false);
            this.setClientDetailsService(clientDetailsService);
            this.setTokenEnhancer(tokenEnhancer());
            PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
            provider.setPreAuthenticatedUserDetailsService(new RefreshAuthServiceWrapper<PreAuthenticatedAuthenticationToken>(
                    userDetailsServiceAdapter));
            this.setAuthenticationManager(new ProviderManager(Arrays.<AuthenticationProvider>asList(provider)));
        }};
        return tokenSerivces;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 通过 /oauth/check_token 检查token，同时返回增强的信息， 当使用url验证token的时候可以返回增强内容
        endpoints.tokenEnhancer(tokenEnhancer()) //配置Opaque的内容增强器
                .tokenServices(tokenSerivces());
    }
}
