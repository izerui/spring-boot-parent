package com.yj2025.oauth2.server.security.provider;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 最后所有登录器都未正常校验成功的情况下,执行最后的拒绝登录验证器
 * 异常处理:
 * 抛出 {@link AuthenticationException} 异常,继续下一个登录验证器,
 * 抛出 {@link AccountStatusException,InternalAuthenticationServiceException} 则直接返回异常
 */
public class DeniedAuthProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        throw new InternalAuthenticationServiceException("用户名或密码错误!");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
