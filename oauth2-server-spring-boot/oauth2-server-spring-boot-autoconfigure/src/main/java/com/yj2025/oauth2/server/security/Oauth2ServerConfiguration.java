package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.server.Oauth2Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证服务器配置
 */
@Configuration
@EnableAuthorizationServer
@AutoConfigureAfter(ServerSecurityConfiguration.class)
public class Oauth2ServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Oauth2Properties oauth2Properties;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(oauth2Properties.getClientId())
                .secret(passwordEncoder.encode(oauth2Properties.getClientSecret()))
                .scopes("all")
                .authorizedGrantTypes("authorization_code", "password", "refresh_token")
                .redirectUris(oauth2Properties.getRedirectUri())
                .accessTokenValiditySeconds(oauth2Properties.getAccessTokenValiditySeconds())
                .refreshTokenValiditySeconds(oauth2Properties.getRefreshTokenValiditySeconds());
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        if (oauth2Properties.getJwt().isEnabled()) {
            List<TokenEnhancer> delegates = new ArrayList<>();
            delegates.add(jwtTokenEnhancer());
            delegates.add(accessTokenConverter());
            TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
            enhancerChain.setTokenEnhancers(delegates); //配置JWT的内容增强器
            endpoints.accessTokenConverter(accessTokenConverter())
                    .tokenEnhancer(enhancerChain);
        }
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(redisTokenStore())
                .reuseRefreshTokens(false) // 无用，标记下
                .tokenServices(new TokenSerivces() {{
                    this.setTokenStore(endpoints.getTokenStore());
                    this.setSupportRefreshToken(true);
                    this.setReuseRefreshToken(false);
                    this.setClientDetailsService(endpoints.getClientDetailsService());
                    this.setTokenEnhancer(endpoints.getTokenEnhancer());
                    this.setAuthenticationManager(authenticationManager);
                }}); // 不重复使用refreshToken， 每次刷新accessToken的时候，同时返回新的刷新token
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //允许表单认证
        security.passwordEncoder(passwordEncoder)
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }


    @Bean
    public RedisTokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }


    @Bean
    @ConditionalOnProperty(name = "oauth2.server.jwt.enabled", havingValue = "true")
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Bean
    @ConditionalOnProperty(name = "oauth2.server.jwt.enabled", havingValue = "true")
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(oauth2Properties.getJwt().getKeyFile(), oauth2Properties.getJwt().getKeyPassword().toCharArray());
        return keyStoreKeyFactory.getKeyPair(oauth2Properties.getJwt().getKeyAlias(), oauth2Properties.getJwt().getKeyPassword().toCharArray());
    }

    @Bean
    @ConditionalOnProperty(name = "oauth2.server.jwt.enabled", havingValue = "true")
    public JwtTokenEnhancer jwtTokenEnhancer() {
        return new JwtTokenEnhancer();
    }

}
