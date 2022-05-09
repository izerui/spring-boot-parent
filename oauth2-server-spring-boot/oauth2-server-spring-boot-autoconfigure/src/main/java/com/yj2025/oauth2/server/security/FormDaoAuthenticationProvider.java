package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.security.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class FormDaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder md5PasswordEncoder;

    public FormDaoAuthenticationProvider(UserDetailsService userDetailsService, BCryptPasswordEncoder md5PasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.md5PasswordEncoder = md5PasswordEncoder;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("请输入密码!");
        }
        User user = (User) userDetails;
        String inputPassword = authentication.getCredentials().toString();
        if (!this.md5PasswordEncoder.matches(inputPassword, user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误!");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("用户已禁用!");
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDetails user = this.getUserDetailsService().loadUserByUsername(username);
        if (user == null) {
            throw new InternalAuthenticationServiceException("用户名或密码错误!");
        }
        return user;
    }

    protected UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

}
