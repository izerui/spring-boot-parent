package com.yj2025.oauth2.opaque;

import com.yj2025.oauth2.security.User;
import com.yj2025.oauth2.server.LoginSuccessHandler;
import com.yj2025.oauth2.server.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class SampleSuccessHandler implements LoginSuccessHandler, LogoutSuccessHandler {
    @Override
    public void onAuthenticationSuccess(User authentication) {
        System.out.println(authentication.getAccountName() + ": 登录成功");
    }

    @Override
    public void revokeTokenSuccess(User authentication) {
        System.out.println(authentication.getAccountName() + ": 登出成功");
    }
}
