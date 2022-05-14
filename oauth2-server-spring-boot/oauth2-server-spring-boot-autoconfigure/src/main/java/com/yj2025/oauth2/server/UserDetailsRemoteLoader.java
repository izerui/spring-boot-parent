package com.yj2025.oauth2.server;

import com.yj2025.oauth2.security.User;
import com.yj2025.oauth2.server.security.provider.UserSelector;

public interface UserDetailsRemoteLoader {
    /**
     * 获取用户,通过request 获取刷新token的usercode，或者其他登录器标识的指定用户信息
     *
     * @param username         用户输入的手机号或者邮箱
     * @param selector 用户选择器
     * @return
     */
    User loadUserByUsername(String username, UserSelector selector);
}
