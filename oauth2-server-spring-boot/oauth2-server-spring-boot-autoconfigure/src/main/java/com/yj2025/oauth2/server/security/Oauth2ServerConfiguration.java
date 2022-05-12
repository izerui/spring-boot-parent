package com.yj2025.oauth2.server.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.yj2025.oauth2.security.User;
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
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
    @ConditionalOnProperty(name = "oauth2.server.jwt.enabled", havingValue = "true")
    public class OpaqueTokenConfig implements ExpandEndpointsConfigurer {

        @Autowired
        private KeyPair keyPair;

        @Bean
        public JwtAccessTokenConverter accessTokenConverter() {
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

        @Bean
        public JwtTokenEnhancer jwtTokenEnhancer() {
            return new JwtTokenEnhancer();
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
            delegates.add(jwtTokenEnhancer());
            delegates.add(accessTokenConverter());
            TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
            enhancerChain.setTokenEnhancers(delegates); //配置JWT的内容增强器
            endpoints.accessTokenConverter(accessTokenConverter())
                    .tokenEnhancer(enhancerChain);
        }
    }

    /**
     * JWT内容增强器
     */
    public static class JwtTokenEnhancer implements TokenEnhancer{
        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            User securityUser = (User) authentication.getPrincipal();
            Map<String, Object> info = new HashMap<>();
            //把用户ID设置到JWT中
            info.put("entCode", securityUser.getEntCode());
            info.put("entName", securityUser.getEntName());
            info.put("accountCode", securityUser.getAccountCode());
            info.put("accountName", securityUser.getAccountName());
            info.put("userCode", securityUser.getUserCode());
            info.put("userName", securityUser.getUserName());
            info.put("serverTime", System.currentTimeMillis() / 1000);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
            return accessToken;
        }

//    public static void main(String[] args) {
//        String s = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2NvdW50Q29kZSI6IjExMDk0NzQzIiwiZW50TmFtZSI6IuWkp-S5lCIsImVudENvZGUiOiIxNDZhMjYwZi0wNWQxLTQ4ZGYtOGUwYy00ODZhNzYwMGIyYWMiLCJhY2NvdW50TmFtZSI6IjE4MDczMTEyMTIwIiwidXNlcl9uYW1lIjoiMTgwNzMxMTIxMjAiLCJzY29wZSI6WyJhbGwiXSwiZXhwIjoxNjIzMzE0NjgwLCJ1c2VyTmFtZSI6IuiKseacqOWFsCIsImF1dGhvcml0aWVzIjpbIlBVUkNIQVNFX1FVT1RFX01BTkFHRSIsIlcwMDA5IiwiS0ZfUEpfMDAxIiwiUDAwMTkiLCJQMDAxNiIsIlAwMDE1IiwiUjAwMjAxIiwiUDAwMTgiLCJSMDAyMDIiLCJSMDAyMDMiLCJSMDAyMDQiLCJXX09NUF8wMl8wMDMiLCJXX09NUF8wMl8wMDIiLCJBX0VJTV8wMSIsIldfT01QXzAyXzAwMSIsIlcwMDExIiwiVzAwMTYiLCJPX1BBWSIsIkZfQUNUX01fMDQiLCJUUUJMMDEiLCJGSU5BTkNFX1JFUE9SVCIsIkZfQUNUX01fMDIiLCJGX0FDVF9NXzAzIiwiUDAwMDgiLCJSMDA2MDEwMSIsIkZfQUNUX01fMDEiLCJQMDAwNyIsIlAwMDAzIiwiT1JERVJfQk9NX01BTkFHRVIiLCJQMDAwMiIsIkhSX0JBU0VfU0VUIiwiQ1VTVE9NRVJfREVWX1BMQU5fU0VUVElORyIsIldfRU9JV0FfMTRfMjAiLCJQQzAwMDEiLCJIMDAwNSIsIkgwMDA0IiwiSDAwMDYiLCJIMDAwMyIsIkgwMDAyIiwiSF9BQU1fMDYiLCJSMDAxMDEiLCJSMDAxMDIiLCJSMDAxMDMiLCJSMDAxMDQiLCJSMDAxMDUiLCJLRl9QSl8wMDFfMDAyIiwiS0ZfUEpfMDAxXzAwMSIsIkZJTkFOQ0UiLCJLRl9QSl8wMDFfMDAzIiwiRl9DTV8wMiIsIkNSTV9JTlFVSVJZX0JBU0VfU0VUVElORyIsIkgwMDE2IiwiSDAwMTUiLCJTQUxFIiwiQTAwMDciLCJIMDAxNyIsIkgwMDExIiwiQTAwMDUiLCJLRl9TWl8wMDEiLCJBMDAwNiIsIkEwMDA0IiwiU1VQUExJRVJfQVVESVQiLCJJRVJQIiwiSDAwMTkiLCJSMDA4IiwiUjAwOSIsIlIwMDYiLCJSMDA3IiwiUjAwNCIsIlIwMDUiLCJSMDAyIiwiUjAwMyIsIlBfU0VUXzAxIiwiQkEwMDAxIiwiSDAwMjEiLCJQX1BBWSIsIkgwMDIwIiwiUjAwNDAxIiwiUjAwNDAyIiwiUjAwNTAyMDEiLCJJTlFVSVJZX1FVT1RfQVVESVQiLCJDUk1fSU5RVUlSWV9ERVZfVE9ETyIsIldfT01QXzAxXzAwMiIsIlBfUEFZX1RBWF8wMSIsIldfT01QXzAxXzAwMSIsIkFETUlOIiwiUjAxMCIsIk5PTkJPTV9QVVJDSEFTRV9BVURJVCIsIkhfQU5NXzA1IiwiUjAwMSIsIlNfVFFCTEdMXzAxIiwiRF9CRk1fMDFfMDEiLCJIX0RQTV8wMyIsIlNVUEVSVklTT1JfU0VUVElORyIsIk1fUE9NXzA2IiwiU1VNTUFSWV9SRVBPUlQiLCJSMDAzMDEiLCJNX1BPTV8wNCIsIlIwMDUwMTAxIiwiTk9OX0JPTV9QVVJDSEFTRSIsIkNPU1RfQVBQTFlfT1JERVJfQVVESVQiLCJTSlMiLCJEX0JGTV8wMV8wMyIsIklOUVVJUllfQVVESVQiLCJDVVNUT01FUl9ERVZfU0VUVElORyIsIkNSTV9JTlFVSVJZX1BSSUNFX1NFVFRJTkciLCJQMDAyNiIsIlAwMDI4IiwiUDAwMjMiLCJQMDAyMiIsIlAwMDI1IiwiTV9BUE9NXzAxX1RSQSIsIlAwMDI0IiwiU0pTX0FVRElUXzAwMSIsIlAwMDIxIiwiREVQVF9NQU5BR0VSIiwiQzAwMDQiLCJDMDAwMSIsIlIwMDQwMjAxIiwiSFIiLCJDT1NUX0FQUExZX0NBTkNFTF9PUkRFUiIsIk1fUE1BTV8wMiIsIklOUVVJUllfREVWX1BVUl9TRVQiLCJNX1BNQU1fMDUiLCJSMDAxMDEwMSIsIlIwMDEwMTAyIiwiQ1VTVE9NRVJfREVWX0FVVEhfU0VUVElORyIsIlBST01PVEVfQVVESVQiLCJSMDA0MDEwMyIsIlIwMDQwMTAxIiwiTk9OQk9NX1BVUkNIQVNFX09SREVSIiwiTV9QT01fMDRfMDciLCJNX1BPTV8wNF8wNiIsIk1fUE9NXzA0XzA1IiwiUjAwMTAyMDIiLCJNX1BPTV8wNF8wNCIsIlIwMDEwMjAxIiwiSU5RVUlSWV9DT0xMRUNUIiwiTV9QT01fMDRfMDkiLCJNX0ZQU1RfMDQiLCJDUk1fSU5RVUlSWV9GT0xMT1dfRk9MREVSIiwiTV9GUFNUXzAzIiwiU19HTFhTRERfMDEiLCJNMDAwNCIsIk0wMDAzIiwiRjAwMDAxIiwiQ1JNX0lOUVVJUllfQVVESVRfQU5EX0NPTExFQ1QiLCJGX0FSTV8xMiIsIkZfQVJNXzExIiwiRl9BUk1fMTMiLCJXX09BTVBfMDQiLCJERVYyMDAzIiwiRDAwMDgiLCJBQ0NFU1NPUklFU19QSUNLIiwiTTAwMDIiLCJEMDAwNSIsIk0wMDAxIiwiUjAwMTAzMDEiLCJzYWxlQ2xhdXNlIiwiV0FSRUhPVVNFIiwiR01fQVVESVQiLCJSMDAyMDQwMSIsIlBST01PVEVfUFJPRFVDVCIsIkZfUl8wMSIsIkZfUl8wMyIsIkZfUl8wNCIsIkNSTSIsIkNSTV9JTlFVSVJZX1BSSUNFIiwiR01fQVVESVRfMDAwMDMiLCJHTV9BVURJVF8wMDAwNCIsIkdNX0FVRElUXzAwMDAxIiwiR01fQVVESVRfMDAwMDIiLCJTUFQwMDAxIiwiREUwMDEiLCJTQUxFLUNIQU5HRS1UQVgtUkFURSIsIlcwMDMxIiwiUjAxMDAxIiwiVzAwMzIiLCJXMDAzMyIsIlcwMDM0IiwiUjAwMTA0MDEiLCJXMDAzNSIsIk0xMzEwIiwiREVWMDA2IiwiREVWMDA1IiwiSF9FQVNfMDQiLCJERVYwMDIiLCJXX0tVUERfMTEiLCJERVYwMDQiLCJERVYwMDMiLCJXX0tVUERfMTQiLCJTSlNfMDAxIiwiREVWMjAxMCIsIlIwMDIwMzAxIiwiUEMiLCJQUk9EVUNUX1BST01PVEUiLCJXMDAyMCIsIlcwMDIxIiwiVzAwMjIiLCJXMDAyNCIsIk0xMjAwIiwiUjAwMzAxMDEiLCJXX0tVUERfMDkiLCJXMDAyNiIsIkFQUCIsIkRFVjM4ODM5MyIsIkZfUl8wMV8wMSIsIk0wMDAxMiIsIk0xMDE1IiwiRl9SXzAxXzAyIiwiTTEwMTgiLCJTSlMwMDEiLCJNX1BPTV8wNl8wMSIsIk0xMDExIiwiUjAwMTA1MDEiLCJNMTAxMiIsIkRFVjM4ODM5NCIsIkNPU1RfQVBQTFlfQ0FOQ0VMIiwiRFpHTDAxIiwiU19TUUFfMDMiLCJNMTAyOCIsIlIwMDIwMjAxIiwiU0FMRV9SRUNFSVZBQkxFX1JFUE9SVCIsIk1fQVBPTV8wMV8wMiIsIk1fQVBPTV8wMV8wNiIsIlIwMDIwMjAzIiwiUjAwMjAyMDIiLCJLRl9IUF8wMDFfMDAxIiwiUE9TVF9SRVBPUlQiLCJXX09NUF8wMiIsIktGX0hQXzAwMV8wMDIiLCJXX09NUF8wMSIsIk0xMDIyIiwiU19SRUMiLCJQX0daU0hfMDEiLCJSMDA5MDEiLCJUT1RBTF9WSUVXIiwiRl9BQ1RfTSIsIk5PTkJPTSIsIlNfU1FNXzAxIiwiRjAwMzciLCJTMDAxMCIsIk0xMDA0IiwiTTEwMDUiLCJNMTAwNyIsIlIwMDIwMTAyIiwiUjAwMjAxMDEiLCJIUEpDWUdIIiwiUjAwMjAxMDMiLCJDUk1fSU5RVUlSWV9DTFVFIiwiRjAwMjIiLCJGMDAyMyIsIkYwMDI0IiwiRjAwMjUiLCJNMTAwMSIsIk0xMDAzIiwiTTMzMDEiLCJTMDAwMSIsIlMwMDAyIiwiTTEyNDEiLCJZWFlXR0xfMDEiLCJDUk1fSU5RVUlSWV9QUklDRV9GT0xERVIiLCJNMTEwNzMiLCJNMTA1MCIsIk0xMDU1IiwiTTEwNTYiLCJNMTA1NyIsIk0xMDU4IiwiTTEwNTEiLCJNMTA1MiIsIk0xMDUzIiwiTTEwNTQiLCJXT1JLQkVOQ0giLCJDVVNUT01FUl9BVURJVCIsIlNBTEVfT1JERVJfQVVESVRfVFJBQ0UiLCJNMzAwMiIsIk0xMDYzIiwiTTExNTgiLCJNMTAzOCIsIlNBTEVfUFJPUE9TQUwiLCJQUk9EVUNUX1BST01PVEVfMSIsIlNfUkVDXzA0IiwiU19SRUNfMDMiLCJTX1JFQ18wMSIsIk0xMDMzIiwiUFJPTU9URV9QUk9EVUNUX0NBVEVHT1JZIiwiTTEwMzUiLCJHRFlGVEdMXzAxIiwiTTEwMzAiLCJIX0RGQV8wMiIsIk0xMDMxIiwiTTEwMzIiLCJQMDAwMDEiLCJNMTA0OSIsIlBfUEFZX1RBWF9BVURJVF8wMSIsIkRFVl9QVVJDSEFTRV9RVU9URV9NQU5BR0UiLCJNMTA0NCIsIk0xMDQwIiwiUFJFUEFSRV9JTlZFTlRPUllfQ0hBTkdFIiwiUjAwNjAxIiwiQ1JNX0lOUVVJUllfREVWX1FVT1RFX0FTU0lHTiIsIkNVU1RPTUVSX0RFVl9UQVNLX01BTkFHRVIiLCJTQUxFX1RBWF9SQVRFX0NIQU5HRV9BVURJVCIsIk9TQzAwMDIiLCJPU0MwMDAxIiwiUkVQT1JUXzAwMTEiLCJSRVBPUlRfMDAxMCIsIkNSTV9JTlFVSVJZX1FVT1RFIiwiTV9GUFNUXzAzXzAwMSIsIlhTRFpZU0tHTF8wMSIsIjMyMDAzMDMwMSIsIk1fRlBTVF8wM18wMDIiLCJXX0tVQ1hfMTMiLCJNMjA3MSIsIldfS1VDWF8xMiIsIlIwMDgwMTAxIiwiSF9QRk1fMDEiLCJSMDA1MDEiLCJTSElQTUVOVDAwMDEiLCJSMDA1MDIiLCJOT05CT01fUFVSQ0hBU0UiLCJNMTA3MCIsIkNSTV9JTlFVSVJZX0ZPTExPVyIsIk0xMDc3IiwiWUoyMDAyIiwiWUoyMDAxIiwiWUoyMDAzIiwiWUoyMDA2IiwiWUoyMDA4IiwiQ1JNX0lOUVVJUllfQVVESVQiLCJSRVBPUlRfMDAwOCIsIlJFUE9SVF8wMDA3IiwiUkVQT1JUXzAwMDYiLCJSRVBPUlRfMDAwNSIsIk1BTlVGQUNUVVJFIiwiWUoyMDExIiwiUkVQT1JUXzAwMDkiLCJZSjIwMTAiLCJQX1NRTV8wMyIsIlBfU1FNXzAyIiwiTTEwODIiLCJOT05CT01fUFVSX0FORF9HRVRfQVVESVQiLCJ0YXNrLXNlZSIsIllKMjAxMiIsIllKMjAxNSIsIlJFUE9SVF8wMDA0IiwiUkVQT1JUXzAwMDMiLCJSRVBPUlRfMDAwMiIsIlJFUE9SVF8wMDAxIiwiWUoyMDE4IiwiR0QwNCIsIkdEMDMiLCJXX0VPSVdBXzEyIiwiUFVSQ0hBU0UiLCJSMDA4MDEiLCJPX1BBWV8wMDQiLCJPX1BBWV8wMDMiLCJPX1BBWV8wMDIiLCJPX1BBWV8wMDEiLCJQX09NUE1fMDEiLCJTMDAwMDYiLCJTMDAwMjIiLCJTMDAwMjEiLCJTMDAwMjMiLCJXX0VPSVdBXzIwIiwiV19FT0lXQV8yMSIsIldfRU9JV0FfMTUiLCJXX0VPSVdBXzE0IiwiUjAwNzAxMDEiLCJZWFlXRkdTWjAxIiwiQ1JNX0lOUVVJUllfREVWX1FVT1QiLCJDUk1fS0ZZR1hfMDEiLCJSMDA3MDEiLCJSMDA3MDIiLCJNX0FQT01fMDEiLCJNUkYwMDAxIiwiTVJGMDAwMiIsInRvdGFsVmlld1JiYWNWaWV3IiwiUkVXT1JLLTAyIiwiUF9QQVlfMDMiLCJQX1BBWV8wMiIsIlBST01PVEVfUFJPRFVDVF8wMSIsIlBfUEFZXzAxIiwiRklOQU5DRV9PUkRFUl9SRVBPUlQiLCJRQTAwMDEiLCJERVZFTE9QTUVOVCIsIkRFVl9QVVJfUVVPVEVfQVVESVQiLCJSMDA3MDIwMSIsIktGX0RBXzAwMV8wMDEiLCJidXNpbmVzcy1zZXR0aW5nIl0sImp0aSI6IjIxZWU5OTA5LWUzNjMtNGY4OS04YmE2LTljZjNhZjMyYjQyNyIsInVzZXJDb2RlIjoiOTI0ZTkzYTEtN2IxMi00M2NjLThhNzEtYjE3OTM4N2Y0NWNmIiwiY2xpZW50X2lkIjoiaWVycC1nYXRld2F5In0.OZuEacc-U9N1F2_2JvFl11RlBl_l4gOHFc899VihL1hwfNrK2A70A7DZWo7ozMAvTXPAF6XE1TsHN2EflgXS6k5PXJxCOoxhp1PbcpjXikCIP1obJRXt9FICoPRmUuBcyTHULac9a15PAUZHJg6dUiJXQJVeY5YnaCPEV5Lo47z0vA5Klg1frhI_G3M_P1wb1TqGYLdREZt_NARS-dQneL1mvbsPepygPdB2f4IV-lAvQoTHxvtUdNkK1qc_C9Zz2ipxHNc7GqSjSVfrlUPpryPYrXRp-wYU0sLVptDgi-s6XLxyngjcn9VoNM1C7OiwJP34HfQoA264f_9wKwhkLw";
//        System.out.println(JWSObject.parse(s));
//    }
    }

}
