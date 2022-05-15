package com.yj2025.oauth2.server.security.provider;

import com.yj2025.oauth2.server.security.UserDetailsServiceAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class RefreshAuthServiceWrapper<T extends Authentication> extends UserDetailsByNameServiceWrapper<T> implements UserSelector {

    private UserDetailsServiceAdapter userDetailsServiceAdapter;

    public RefreshAuthServiceWrapper(UserDetailsServiceAdapter userDetailsServiceAdapter) {
        super(userDetailsServiceAdapter);
        this.userDetailsServiceAdapter = userDetailsServiceAdapter;
    }

    @Override
    public UserDetails loadUserDetails(T authentication) throws UsernameNotFoundException {
        return userDetailsServiceAdapter.loadUserByUsername(authentication.getName(), this);
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
