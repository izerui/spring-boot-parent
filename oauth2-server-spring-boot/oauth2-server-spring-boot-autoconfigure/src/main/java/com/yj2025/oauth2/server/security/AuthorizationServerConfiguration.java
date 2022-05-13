package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.server.Oauth2Properties;
import com.yj2025.oauth2.server.security.jwt.JwtTokenConfiguration;
import com.yj2025.oauth2.server.security.opaque.OpaqueTokenConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Arrays;

/**
 * 认证服务器配置
 */
@Configuration
@EnableAuthorizationServer
@AutoConfigureAfter(SecurityConfiguration.class)
@Import({OpaqueTokenConfiguration.class, JwtTokenConfiguration.class})
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Oauth2Properties properties;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectProvider<ExpandEndpointsConfigurer> expandEndpointsConfigurers;

    @Bean
    public RedisTokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Bean
    public TokenInfoEnhancer tokenInfoEnhancer() {
        return new TokenInfoEnhancer();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(properties.getClientId())
                .secret(passwordEncoder.encode(properties.getClientSecret()))
                .scopes("all")
                .authorizedGrantTypes("authorization_code", "password", "refresh_token")
                .accessTokenValiditySeconds(properties.getAccessTokenValiditySeconds())
                .refreshTokenValiditySeconds(properties.getRefreshTokenValiditySeconds());
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        ExpandEndpointsConfigurer expandEndpointsConfigurer = expandEndpointsConfigurers.getIfAvailable();
        if (expandEndpointsConfigurer != null) {
            expandEndpointsConfigurer.configure(endpoints);
        }
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(redisTokenStore())
                .userDetailsService(userDetailsService)
                .reuseRefreshTokens(false) // 无用，标记下
                .tokenServices(new TokenSerivces() {{
                    this.setTokenStore(endpoints.getTokenStore());
                    this.setSupportRefreshToken(true);
                    this.setReuseRefreshToken(false);
                    this.setClientDetailsService(endpoints.getClientDetailsService());
                    this.setTokenEnhancer(endpoints.getTokenEnhancer());
                    PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
                    provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>(
                            userDetailsService));
                    this.setAuthenticationManager(new ProviderManager(Arrays.<AuthenticationProvider>asList(provider)));
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


}
