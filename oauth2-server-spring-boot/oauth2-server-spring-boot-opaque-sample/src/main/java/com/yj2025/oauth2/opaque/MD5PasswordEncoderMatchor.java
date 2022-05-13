package com.yj2025.oauth2.opaque;

import com.yj2025.oauth2.opaque.password.Md5PasswordEncoder;
import com.yj2025.oauth2.server.PasswordEncoderMatchor;

public class MD5PasswordEncoderMatchor implements PasswordEncoderMatchor {

    private Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

    @Override
    public String encode(String rawPassword, String additionalSalt) {
        System.out.println("用盐: " + additionalSalt + " 进行加密");
        return passwordEncoder.encodePassword(rawPassword, additionalSalt);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword, String additionalSalt) {
        System.out.println("用盐: " + additionalSalt + " 进行加密匹配");
        return passwordEncoder.isPasswordValid(encodedPassword, rawPassword, additionalSalt);
    }
}
