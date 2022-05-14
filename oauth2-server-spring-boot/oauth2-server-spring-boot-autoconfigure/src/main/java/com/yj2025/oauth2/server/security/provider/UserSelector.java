package com.yj2025.oauth2.server.security.provider;

import java.util.Optional;

public interface UserSelector {

    Optional<String> getSelector();

    SelectorType getType();

    enum SelectorType {
        NONE_SELECTOR, // 无账号选择器
        USERCODE_SELECTOR, // 用户编码选择器
        ENTCODE_SELECTOR; // 账套编号选择器
    }

    UserSelector NONE_SELECTOR = new UserSelector() {
        @Override
        public Optional<String> getSelector() {
            return Optional.empty();
        }

        @Override
        public SelectorType getType() {
            return SelectorType.NONE_SELECTOR;
        }
    };
}
