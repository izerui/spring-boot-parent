package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.server.Oauth2Properties;
import com.yj2025.oauth2.server.security.jwt.JwtTokenConfiguration;
import com.yj2025.oauth2.server.security.opaque.OpaqueTokenConfiguration;
import com.yj2025.oauth2.server.utils.ExceptionUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 认证服务器配置
 */
@Configuration
@EnableAuthorizationServer
@AutoConfigureAfter(SecurityConfiguration.class)
@Import({OpaqueTokenConfiguration.class, JwtTokenConfiguration.class})
public class Oauth2Configuration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Oauth2Properties properties;
    @Autowired
    private UserDetailsServiceAdapter userDetailsServiceAdapter;
    @Autowired
    private ObjectProvider<ExpandEndpointsConfigurer> expandEndpointsConfigurers;
    @Value("${spring.application.name:'oauth2'}")
    private String applicationName;

    @Bean
    public RedisTokenStore redisTokenStore() {
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(applicationName);
        return redisTokenStore;
    }


    @Bean
    public ClientDetailsService clientDetailsService() {
        try {
            InMemoryClientDetailsServiceBuilder inMemory = new InMemoryClientDetailsServiceBuilder();
            inMemory.withClient(properties.getClientId())
                    .secret(passwordEncoder.encode(properties.getClientSecret()))
                    .scopes("all")
                    .authorizedGrantTypes("authorization_code", "password", "refresh_token")
                    .accessTokenValiditySeconds(properties.getAccessTokenValiditySeconds())
                    .refreshTokenValiditySeconds(properties.getRefreshTokenValiditySeconds());
            return inMemory.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(redisTokenStore())
                .userDetailsService(userDetailsServiceAdapter);
        expandEndpointsConfigurers.ifAvailable(expandEndpointsConfigurer -> {
            ExceptionUtils.wrapExceptions(() -> expandEndpointsConfigurer.configure(endpoints));
        });
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //允许表单认证
        security.passwordEncoder(passwordEncoder)
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }


}
