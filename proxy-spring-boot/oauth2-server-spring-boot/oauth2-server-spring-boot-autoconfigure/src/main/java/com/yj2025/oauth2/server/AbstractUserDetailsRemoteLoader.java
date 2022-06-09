package com.yj2025.oauth2.server;

import com.yj2025.oauth2.security.support.User;
import com.yj2025.oauth2.server.security.provider.UserSelector;

/**
 * @author liuyuhua
 * @date 2022/5/19
 */
public abstract class AbstractUserDetailsRemoteLoader implements UserDetailsRemoteLoader {

    @Override
    public User loadUserByUsername(String username, UserSelector selector) {
        switch (selector.getSelectorType()) {
            case USER_CODE_SELECTOR:
                if (selector.getSelector().isPresent()) {
                    return loadUserBySelectUsercode(username, selector.getSelector().get());
                } else {
                    return loadUserByUsername(username);
                }
            case ENT_CODE_SELECTOR:
                return loadUserBySelectEntcode(username, selector.getSelector().get());
            default:
                return loadUserByUsername(username);
        }
    }

    protected abstract User loadUserBySelectEntcode(String username, String entcode);

    protected abstract User loadUserBySelectUsercode(String username, String usercode);

    protected abstract User loadUserByUsername(String username);
}
