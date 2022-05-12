package com.yj2025.oauth2.server.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.yj2025.oauth2.server.Oauth2Properties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

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

    @Configuration
    @ConditionalOnProperty(name = "oauth2.server.jwt.enabled", matchIfMissing = true, havingValue = "false")
    public class OpaqueTokenConfig implements ExpandEndpointsConfigurer {

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            // 通过 /oauth/check_token 检查token，同时返回增强的信息， 当使用url验证token的时候可以返回增强内容
            endpoints.tokenEnhancer(tokenInfoEnhancer()); //配置Opaque的内容增强器
        }
    }


    @Configuration
    @ConditionalOnProperty(name = "oauth2.server.jwt.enabled", havingValue = "true")
    public class JwtTokenConfig implements ExpandEndpointsConfigurer {

        @Autowired
        private KeyPair keyPair;

        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
            jwtAccessTokenConverter.setKeyPair(keyPair());
            return jwtAccessTokenConverter;
        }

        @Bean
        public KeyPair keyPair() {
            //从classpath下的证书中获取秘钥对
            KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(oauth2Properties.getJwt().getKeyFile(), oauth2Properties.getJwt().getKeyPassword().toCharArray());
            return keyStoreKeyFactory.getKeyPair(oauth2Properties.getJwt().getKeyAlias(), oauth2Properties.getJwt().getKeyPassword().toCharArray());
        }

        @ResponseBody
        @GetMapping("/rsa/key")
        public Map<String, Object> getKey() {
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAKey key = new RSAKey.Builder(publicKey).build();
            return new JWKSet(key).toJSONObject();
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            List<TokenEnhancer> delegates = new ArrayList<>();
            delegates.add(tokenInfoEnhancer());
            delegates.add(jwtAccessTokenConverter());
            TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
            enhancerChain.setTokenEnhancers(delegates); //配置JWT的内容增强器
            endpoints.accessTokenConverter(jwtAccessTokenConverter())
                    .tokenEnhancer(enhancerChain);
        }
    }


}
