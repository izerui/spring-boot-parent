package com.yj2025.open.oauth;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.yj2025.open.commons.ClientStore;
import com.yj2025.open.commons.RedisClientStore;
import com.yj2025.open.oauth.provider.ClientProvider;
import com.yj2025.open.oauth.security.ClientDefination;
import com.yj2025.open.oauth.security.JwtTokenEnhancer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * oauth2认证服务器配置
 *
 * @author liuyuhua
 */
@Configuration
@Import(SecurityConfig.class)
@EnableAuthorizationServer
@EnableConfigurationProperties(Oauth2ServerProperties.class)
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ObjectProvider<ClientProvider> clientProviders;
    @Autowired
    private Oauth2ServerProperties properties;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientId -> {
            ClientProvider provider = clientProviders.getIfAvailable();
            Assert.notNull(provider, "必须存在一个类型为 com.yj2025.open.oauth.provider.ClientProvider 的Bean");
            String clientSecret = provider.getClientSecret(clientId);
            Assert.notNull(clientSecret, "未找到对应的clientId");
            ClientDefination clientDefination = new ClientDefination(clientId, passwordEncoder.encode(clientSecret));
            clientDefination.setAccessTokenValiditySeconds(properties.getAccessTokenValiditySeconds());
            return clientDefination;
        });
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(jwtTokenStore())
                .accessTokenConverter(accessTokenConverter())
                .tokenEnhancer(jwtTokenEnhancer());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //允许表单认证
        security
                .passwordEncoder(passwordEncoder)
                // 允许获取公钥用于oauth2客户端认证对接: /oauth/token_key
                .tokenKeyAccess("permitAll()")
                // 验证token
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }


    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(properties.getJwt().getCaFile(), properties.getJwt().getPassword().toCharArray());
        return keyStoreKeyFactory.getKeyPair(properties.getJwt().getAlias(), properties.getJwt().getPassword().toCharArray());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ClientStore clientStore(RedisConnectionFactory redisConnectionFactory) {
        return new RedisClientStore(redisConnectionFactory);
    }

    @Bean
    public TokenEnhancer jwtTokenEnhancer() {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(new JwtTokenEnhancer(clientProviders));
        delegates.add(accessTokenConverter());
        //配置JWT的内容增强器
        enhancerChain.setTokenEnhancers(delegates);
        return enhancerChain;
    }

    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

}
