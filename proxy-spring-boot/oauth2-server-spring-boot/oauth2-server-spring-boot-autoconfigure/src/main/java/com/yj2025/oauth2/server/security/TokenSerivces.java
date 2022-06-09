package com.yj2025.oauth2.server.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 每次请求token都返回新的
 */
public class TokenSerivces extends DefaultTokenServices {

    @Transactional
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        Field tokenStoreField = ReflectionUtils.findField(DefaultTokenServices.class, "tokenStore");
        tokenStoreField.setAccessible(true);
        TokenStore tokenStore = (TokenStore) ReflectionUtils.getField(tokenStoreField, this);
        OAuth2RefreshToken refreshToken = invokeCreateRefreshToken(authentication);
        OAuth2AccessToken accessToken = invodeCreateAccessToken(authentication, refreshToken);
        tokenStore.storeAccessToken(accessToken, authentication);
        // In case it was modified
        refreshToken = accessToken.getRefreshToken();
        if (refreshToken != null) {
            tokenStore.storeRefreshToken(refreshToken, authentication);
        }
        return accessToken;
    }

    private OAuth2RefreshToken invokeCreateRefreshToken(OAuth2Authentication authentication) {
        Method method = ReflectionUtils.findMethod(TokenSerivces.class, "createRefreshToken", OAuth2Authentication.class);
        method.setAccessible(true);
        return (OAuth2RefreshToken) ReflectionUtils.invokeMethod(method, this, authentication);
    }

    private OAuth2AccessToken invodeCreateAccessToken(OAuth2Authentication authentication, OAuth2RefreshToken refreshToken) {
        Method method = ReflectionUtils.findMethod(TokenSerivces.class, "createAccessToken", OAuth2Authentication.class, OAuth2RefreshToken.class);
        method.setAccessible(true);
        return (OAuth2AccessToken) ReflectionUtils.invokeMethod(method, this, authentication, refreshToken);
    }

}
