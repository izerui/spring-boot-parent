package com.yj2025.server.sample;

import com.yj2025.oauth2.security.User;
import com.yj2025.oauth2.server.LoginSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class SampleLoginSuccessHandler implements LoginSuccessHandler {
    @Override
    public void onAuthenticationSuccess(User authentication) {
        System.out.println(authentication.getAccountName() + ": 登录成功");
    }
}
