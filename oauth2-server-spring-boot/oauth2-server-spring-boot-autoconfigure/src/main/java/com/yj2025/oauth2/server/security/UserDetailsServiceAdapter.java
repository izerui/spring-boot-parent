package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.server.UserDetailsRemoteLoader;
import com.yj2025.oauth2.server.security.provider.UserSelector;
import com.yj2025.oauth2.server.security.provider.QrcodeService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 用户加载适配器
 */
public class UserDetailsServiceAdapter implements UserDetailsService {

    private ObjectProvider<UserDetailsRemoteLoader> userDetailsRemoteLoaderObjectProvider;

    public UserDetailsServiceAdapter(ObjectProvider<UserDetailsRemoteLoader> userDetailsRemoteLoaderObjectProvider) {
        this.userDetailsRemoteLoaderObjectProvider = userDetailsRemoteLoaderObjectProvider;
    }

    public UserDetails loadUserByUsername(String username, UserSelector selector) throws UsernameNotFoundException {
        return userDetailsRemoteLoaderObjectProvider.getIfAvailable().loadUserByUsername(username, selector);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRemoteLoaderObjectProvider.getIfAvailable().loadUserByUsername(username, UserSelector.NONE_SELECTOR);
    }


}
