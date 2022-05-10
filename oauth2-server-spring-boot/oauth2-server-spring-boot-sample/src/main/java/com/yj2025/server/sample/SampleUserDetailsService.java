package com.yj2025.server.sample;

import com.yj2025.oauth2.security.User;
import com.yj2025.oauth2.server.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Component
public class SampleUserDetailsService implements UserDetailsLoader {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User loadUserByUsername(String username, @Nullable String usercode) {
        if (usercode != null) {
            System.out.println("切换用户: " + usercode);
            return new User(usercode + "001", passwordEncoder.encode("123456"), Collections.emptyList());
        }
        return new User("test", passwordEncoder.encode("123456"), Collections.emptyList());
    }
}
