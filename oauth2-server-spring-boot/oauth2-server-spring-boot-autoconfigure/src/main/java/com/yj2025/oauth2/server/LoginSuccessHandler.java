package com.yj2025.oauth2.server;

import com.yj2025.oauth2.security.User;

public interface LoginSuccessHandler {

    /**
     * 登录成功回调
     * @param authentication
     */
    void onAuthenticationSuccess(User authentication);
}
