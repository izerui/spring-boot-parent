package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.server.UserDetailsRemoteLoader;
import com.yj2025.oauth2.server.security.provider.UserSelector;
import com.yj2025.oauth2.server.security.provider.QrcodeService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceAdapter implements UserDetailsService {

    private UserDetailsRemoteLoader userDetailsLoader;
    private QrcodeService qrcodeService;

    public UserDetailsServiceAdapter(UserDetailsRemoteLoader userDetailsLoader, QrcodeService qrcodeService) {
        this.userDetailsLoader = userDetailsLoader;
        this.qrcodeService = qrcodeService;
    }

    public UserDetails loadUserByUsername(String username, UserSelector selector) throws UsernameNotFoundException {
        return userDetailsLoader.loadUserByUsername(username, selector);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsLoader.loadUserByUsername(username, UserSelector.NONE_SELECTOR);
    }


}
