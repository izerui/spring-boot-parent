package com.yj2025.websocket.server;

@FunctionalInterface
public interface UserNameLoader {
    /**
     * 根据用户编号获取用户名
     * @param userCode
     * @return
     */
    String getUserName(String userCode);
}
