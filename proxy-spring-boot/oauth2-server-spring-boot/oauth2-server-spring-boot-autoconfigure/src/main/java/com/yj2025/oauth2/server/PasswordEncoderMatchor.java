package com.yj2025.oauth2.server;

import org.springframework.lang.Nullable;

/**
 * 用户密码加密、校验器
 */
public interface PasswordEncoderMatchor {

    /**
     * 将原文加密，返回加密后的密文
     * @param rawPassword 原文内容
     * @param additionalSalt 额外的盐(可能为空)
     * @return
     */
    String encode(String rawPassword, @Nullable String additionalSalt);

    /**
     * 将原文加密后跟传入的密文比较是否相同
     * @param rawPassword 原文内容
     * @param encodedPassword 传入的密文
     * @param additionalSalt 额外的盐(可能为空)
     * @return
     */
    boolean matches(String rawPassword, String encodedPassword, @Nullable String additionalSalt);
}
