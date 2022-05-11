package com.yj2025.oauth2.server;

import com.yj2025.oauth2.security.User;

public interface LoginSuccessHandler {

    void onAuthenticationSuccess(User authentication);
}
