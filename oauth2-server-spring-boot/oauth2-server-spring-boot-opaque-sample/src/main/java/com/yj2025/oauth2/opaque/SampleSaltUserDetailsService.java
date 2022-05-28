package com.yj2025.oauth2.opaque;

import com.yj2025.oauth2.security.support.User;
import com.yj2025.oauth2.server.PasswordEncoderMatchor;
import com.yj2025.oauth2.server.UserDetailsRemoteLoader;
import com.yj2025.oauth2.server.security.provider.UserSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleSaltUserDetailsService implements UserDetailsRemoteLoader {

    private final static String salt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Autowired
    private PasswordEncoderMatchor passwordEncoderMatchor;

    @Override
    public User loadUserByUsername(String username, UserSelector selector) {
        User demoUser = new User("test", passwordEncoderMatchor.encode("123456", salt), "postCode005", "postCode006")
                .setAdditionalSalt(salt);
        switch (selector.getSelectorType()) {
            case USER_CODE_SELECTOR:
                if (selector.getSelector().isPresent()) {
                    System.out.println("切换用户: " + selector.getSelector().get());
                    return new User(selector.getSelector().get() + "001", passwordEncoderMatchor.encode("123456", salt), "postCode001", "postCode002")
                            .setAdditionalSalt(salt);
                } else {
                    return demoUser;
                }
            case ENT_CODE_SELECTOR:
                return demoUser;
            default:
                return demoUser;
        }
    }
}
