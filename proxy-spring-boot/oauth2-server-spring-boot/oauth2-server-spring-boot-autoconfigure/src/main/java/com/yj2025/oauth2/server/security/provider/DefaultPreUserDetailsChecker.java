package com.yj2025.oauth2.server.security.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

@Slf4j
public class DefaultPreUserDetailsChecker implements UserDetailsChecker {
    @Override
    public void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            log.debug("User account is locked");

            throw new LockedException("用户帐号被锁定");
        }

        if (!user.isEnabled()) {
            log.debug("User account is disabled");

            throw new DisabledException("用户被禁用");
        }

        if (!user.isAccountNonExpired()) {
            log.debug("User account is expired");

            throw new AccountExpiredException("用户帐号已过期");
        }
    }
}
