package com.yj2025.oauth2.jwt;

import com.yj2025.oauth2.security.support.User;
import com.yj2025.oauth2.server.PasswordEncoderMatchor;
import com.yj2025.oauth2.server.UserDetailsRemoteLoader;
import com.yj2025.oauth2.server.security.provider.UserSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleUserDetailsService implements UserDetailsRemoteLoader {

    @Autowired
    private PasswordEncoderMatchor passwordEncoderMatchor;

    @Override
    public User loadUserByUsername(String username, UserSelector selector) {
        User defaultDemoUser = new User("test", passwordEncoderMatchor.encode("123456", null), "postCode005", "postCode006");
        switch (selector.getSelectorType()) {
            case USER_CODE_SELECTOR:
                if (selector.getSelector().isPresent()) {
                    System.out.println("切换用户: " + selector.getSelector().get());
                    return new User(selector.getSelector().get() + "-" + UserSelector.SelectorType.USER_CODE_SELECTOR.name(), passwordEncoderMatchor.encode("123456", null), "postCode001", "postCode002");
                }
                return defaultDemoUser;
            case ENT_CODE_SELECTOR:
                System.out.println("扫码登录entCode: " + selector.getSelector().get());
                return defaultDemoUser;
            default:
                return defaultDemoUser;
        }
    }
}
