package com.yj2025.server.sample;

import com.yj2025.oauth2.security.User;
import com.yj2025.oauth2.server.UserDetailsRemoteLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Component
public class SampleUserDetailsService implements UserDetailsRemoteLoader {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User loadUserByUsername(String username, Optional<String> usercode) {
        if (usercode.isPresent()) {
            System.out.println("切换用户: " + usercode.get());
            return new User(usercode.get() + "001", passwordEncoder.encode("123456"), AuthorityUtils.createAuthorityList("postCode001", "postCode002"));
        }
        return new User("test", passwordEncoder.encode("123456"), AuthorityUtils.createAuthorityList("postCode001", "postCode002"));
    }
}
