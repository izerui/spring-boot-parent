package com.yj2025.oauth2.server;

import com.yj2025.oauth2.security.User;

public interface UserDetailsLoader {
    User loadUserByUsername(String username);
}
