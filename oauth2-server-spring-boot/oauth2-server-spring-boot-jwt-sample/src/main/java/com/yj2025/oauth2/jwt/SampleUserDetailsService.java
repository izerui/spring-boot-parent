package com.yj2025.oauth2.jwt;

import com.yj2025.oauth2.security.User;
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
        User demoUser = new User("test", passwordEncoderMatchor.encode("123456", null), "postCode001", "postCode002");
        switch (selector.getType()) {
            case USERCODE_SELECTOR:
                if (selector.getSelector().isPresent()) {
                    System.out.println("切换用户: " + selector.getSelector().get());
                    return new User(selector.getSelector().get() + "001", passwordEncoderMatchor.encode("123456", null), "postCode001", "postCode002");
                } else {
                    return demoUser;
                }
            case ENTCODE_SELECTOR:
                return demoUser;
            default:
                return demoUser;
        }
    }
}
