package com.yj2025.oauth2.server;

import com.yj2025.oauth2.security.support.User;

@FunctionalInterface
public interface LogoutSuccessHandler {
    /**
     * 登出成功回调
     *
     * @param authentication
     */
    void revokeTokenSuccess(User authentication);
}
