package com.yj2025.basic.support;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 建议使用: {@link CacheWrapperAware}
 */
@Deprecated
public interface UserNameAware {

    ThreadLocal<Map<String, String>> THREAD_LOCAL = new InheritableThreadLocal<>();

    String getUserCode();

    void setUserName(String userName);

    default <T> T wrapUserName(Function<T, String> nameGetter) {
        Map<String, String> userMap = THREAD_LOCAL.get();
        if (userMap == null) {
            userMap = new HashMap<>();
            THREAD_LOCAL.set(userMap);
        }
        if (this.getUserCode() != null) {
            if (!userMap.containsKey(this.getUserCode())) {
                String userName = nameGetter.apply((T) this);
                if (userName != null) {
                    userMap.put(this.getUserCode(), userName);
                } else {
                    userMap.put(this.getUserCode(), null);
                }
            }
            this.setUserName(userMap.get(this.getUserCode()));
        }
        return (T) this;
    }
}
