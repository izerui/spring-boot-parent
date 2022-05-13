package com.yj2025.oauth2.opaque;

import com.yj2025.oauth2.security.User;
import com.yj2025.oauth2.server.PasswordEncoderMatchor;
import com.yj2025.oauth2.server.UserDetailsRemoteLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SampleSaltUserDetailsService implements UserDetailsRemoteLoader {

    private final static String salt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Autowired
    private PasswordEncoderMatchor passwordEncoderMatchor;

    @Override
    public User loadUserByUsername(String username, Optional<String> usercode) {
        if (usercode.isPresent()) {
            System.out.println("切换用户: " + usercode.get());
            return new User(usercode.get() + "001", passwordEncoderMatchor.encode("123456", salt), "postCode001", "postCode002")
                    .setAdditionalSalt(salt);
        }
        return new User("test", passwordEncoderMatchor.encode("123456", salt), "postCode001", "postCode002")
                .setAdditionalSalt(salt);
    }
}
