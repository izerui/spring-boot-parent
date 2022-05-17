package com.yj2025.oauth2.server.security.provider;

import java.util.Optional;

public interface UserSelector {

    /**
     * 加载用户的时候，可选的指定选择项，比如指定usercode、entcode
     * @return
     */
    Optional<String> getSelector();

    /**
     * 选择器类型
     * @return
     */
    SelectorType getSelectorType();

    enum SelectorType {
        NONE_SELECTOR, // 无账号选择器
        USER_CODE_SELECTOR, // 用户编码选择器
        ENT_CODE_SELECTOR; // 账套编号选择器
    }

    UserSelector NONE_SELECTOR = new UserSelector() {
        @Override
        public Optional<String> getSelector() {
            return Optional.empty();
        }

        @Override
        public SelectorType getSelectorType() {
            return SelectorType.NONE_SELECTOR;
        }
    };
}
