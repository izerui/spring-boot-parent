package com.yj2025.oauth2.server.security.provider;

import com.yj2025.oauth2.security.support.User;
import com.yj2025.oauth2.server.PasswordEncoderMatchor;
import com.yj2025.oauth2.server.security.UserDetailsServiceAdapter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 密码登录验证器
 */
public class PasswordAuthProvider extends AbstractUserDetailsAuthenticationProvider implements UserSelector {

    private UserDetailsServiceAdapter userDetailsServiceAdapter;
    private ObjectProvider<PasswordEncoderMatchor> passwordCheckMatchorProvider;

    public PasswordAuthProvider(UserDetailsServiceAdapter userDetailsServiceAdapter, ObjectProvider<PasswordEncoderMatchor> passwordCheckMatchorProvider) {
        this.userDetailsServiceAdapter = userDetailsServiceAdapter;
        this.passwordCheckMatchorProvider = passwordCheckMatchorProvider;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("请输入密码!");
        }
        User user = (User) userDetails;
        String inputPassword = authentication.getCredentials().toString();
        if (!this.passwordCheckMatchorProvider.getIfAvailable().matches(inputPassword, user.getPassword(), user.getAdditionalSalt())) {
            throw new BadCredentialsException("用户名或密码错误!");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("用户已禁用!");
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDetails user = this.userDetailsServiceAdapter.loadUserByUsername(username, this);
        if (user == null) {
            throw new InternalAuthenticationServiceException("用户名或密码错误!");
        }
        return user;
    }

    @Override
    public Optional<String> getSelector() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            return Optional.ofNullable(request.getParameter("usercode"));
        }
        return Optional.empty();
    }

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.USER_CODE_SELECTOR;
    }
}
