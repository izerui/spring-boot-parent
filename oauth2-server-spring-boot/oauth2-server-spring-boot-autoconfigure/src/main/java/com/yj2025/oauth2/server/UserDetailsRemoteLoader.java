package com.yj2025.oauth2.server;

import com.yj2025.oauth2.security.User;
import org.springframework.lang.Nullable;

public interface UserDetailsRemoteLoader {
    /**
     * 获取用户
     *
     * @param username 用户输入的手机号或者邮箱
     * @param usercode 指定的用户代码
     * @return
     */
    User loadUserByUsername(String username, @Nullable String usercode);
}
