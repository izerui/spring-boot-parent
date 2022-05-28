package com.yj2025.oauth2.server.security.provider;

import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.www.NonceExpiredException;

/**
 * 异常处理:
 * 抛出 {@link AuthenticationException} 异常,继续下一个登录验证器,
 * 抛出 {@link AccountStatusException,InternalAuthenticationServiceException} 则直接返回异常
 */
public abstract class AbstractAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {


    protected void throwInsufficientAuthenticationExceptionNext(String message) {
        throw new InsufficientAuthenticationException(message);
    }

    protected void throwBadCredentialsExceptionNext(String message) {
        throw new BadCredentialsException(message);
    }

    protected void throwUsernameNotFoundExceptionNext(String message) {
        throw new UsernameNotFoundException(message);
    }

    protected void throwSessionAuthenticationExceptionNext(String message) {
        throw new SessionAuthenticationException(message);
    }

    protected void throwAuthenticationServiceExceptionNext(String message) {
        throw new AuthenticationServiceException(message);
    }

    protected void throwProviderNotFoundExceptionNext(String message) {
        throw new ProviderNotFoundException(message);
    }

    protected void throwPreAuthenticatedCredentialsNotFoundExceptionNext(String message) {
        throw new PreAuthenticatedCredentialsNotFoundException(message);
    }

    protected void throwOAuth2AuthenticationExceptionNext(OAuth2Error error) {
        throw new OAuth2AuthenticationException(error);
    }

    protected void throwAuthenticationCredentialsNotFoundExceptionNext(String message) {
        throw new AuthenticationCredentialsNotFoundException(message);
    }

    protected void throwNonceExpiredExceptionNext(String message) {
        throw new NonceExpiredException(message);
    }

    protected void throwRememberMeAuthenticationExceptionNext(String message) {
        throw new RememberMeAuthenticationException(message);
    }

    protected void throwUnapprovedClientAuthenticationExceptionNext(String message) {
        throw new UnapprovedClientAuthenticationException(message);
    }

    protected void throwAccessTokenRequiredExceptionNext(OAuth2ProtectedResourceDetails resource) {
        throw new AccessTokenRequiredException(resource);
    }

    protected void throwInternalAuthenticationServiceExceptionNext(String message) {
        throw new InternalAuthenticationServiceException(message);
    }

    protected void throwInvalidBearerTokenExceptionNext(String message) {
        throw new InvalidBearerTokenException(message);
    }

    protected void throwCookieTheftExceptionNext(String message) {
        throw new CookieTheftException(message);
    }

    protected void throwInvalidCookieExceptionNext(String message) {
        throw new InvalidCookieException(message);
    }

    protected void throwLockedExceptionNext(String message) {
        throw new LockedException(message);
    }

    protected void throwDisabledExceptionNext(String message) {
        throw new DisabledException(message);
    }

    protected void throwCredentialsExpiredExceptionNext(String message) {
        throw new CredentialsExpiredException(message);
    }

    protected void throwAccountExpiredExceptionNext(String message) {
        throw new AccountExpiredException(message);
    }

    protected void throwAuthenticationExceptionNext(AuthenticationException exception) {
        throw exception;
    }

    protected void throwAccountExpiredExceptionBlock(String message) {
        throw new AccountExpiredException(message);
    }

    protected void throwCredentialsExpiredExceptionBlock(String message) {
        throw new CredentialsExpiredException(message);
    }

    protected void throwDisabledExceptionBlock(String message) {
        throw new DisabledException(message);
    }

    protected void throwLockedExceptionBlock(String message) {
        throw new LockedException(message);
    }

    protected void throwInternalAuthenticationServiceExceptionBlock(String message) {
        throw new InternalAuthenticationServiceException(message);
    }

}
