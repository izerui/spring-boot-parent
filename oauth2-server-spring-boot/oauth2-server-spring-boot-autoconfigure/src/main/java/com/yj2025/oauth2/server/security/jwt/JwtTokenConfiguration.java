package com.yj2025.oauth2.server.security.jwt;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.yj2025.oauth2.security.support.MappingUrls;
import com.yj2025.oauth2.server.Oauth2Properties;
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
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Configuration
@FrameworkEndpoint
@ConditionalOnProperty(name = "oauth2.server.jwt.enabled", havingValue = "true")
public class JwtTokenConfiguration implements ExpandEndpointsConfigurer {

    @Autowired
    private RedisTokenStore redisTokenStore;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private UserDetailsServiceAdapter userDetailsServiceAdapter;
    @Autowired
    private Oauth2Properties properties;

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(properties.getJwt().getKeyFile(), properties.getJwt().getKeyPassword().toCharArray());
        return keyStoreKeyFactory.getKeyPair(properties.getJwt().getKeyAlias(), properties.getJwt().getKeyPassword().toCharArray());
    }

    @ResponseBody
    @GetMapping(MappingUrls.JWT_RSA_KEY_URL)
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair().getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(new TokenInfoEnhancer());
        delegates.add(jwtAccessTokenConverter());
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(delegates); //配置JWT的内容增强器
        return enhancerChain;
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
        endpoints.accessTokenConverter(jwtAccessTokenConverter())
                .tokenServices(tokenSerivces())
                .tokenEnhancer(tokenEnhancer());
    }
}
