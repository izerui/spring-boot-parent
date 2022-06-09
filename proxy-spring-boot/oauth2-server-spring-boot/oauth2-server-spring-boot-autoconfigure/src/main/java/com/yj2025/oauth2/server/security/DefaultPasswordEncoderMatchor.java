package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.server.PasswordEncoderMatchor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DefaultPasswordEncoderMatchor implements PasswordEncoderMatchor {

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public DefaultPasswordEncoderMatchor(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String encode(String rawPassword, String additionalSalt) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword, String additionalSalt) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
