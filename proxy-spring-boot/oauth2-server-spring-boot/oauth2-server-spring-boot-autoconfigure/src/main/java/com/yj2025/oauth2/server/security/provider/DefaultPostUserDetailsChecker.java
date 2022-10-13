package com.yj2025.oauth2.server.security.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

@Slf4j
public class DefaultPostUserDetailsChecker implements UserDetailsChecker {
    @Override
    public void check(UserDetails user) {
        if (!user.isCredentialsNonExpired()) {
            log.debug("User account credentials have expired");

            throw new CredentialsExpiredException("用户帐户凭据已过期");
        }
    }
}
