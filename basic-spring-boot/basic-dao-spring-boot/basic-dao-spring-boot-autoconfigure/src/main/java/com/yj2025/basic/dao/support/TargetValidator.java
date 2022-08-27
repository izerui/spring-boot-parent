package com.yj2025.basic.dao.support;

import java.util.List;

/**
 * 验证是否允许变更为目标对象
 *
 * @param <T>
 */
public interface TargetValidator<T> {

    List<T> allowedTargets();

    default void validateTarget(T t) {
        List<T> targets = allowedTargets();
        if (targets != null && !targets.contains(this)) {
            throw new RuntimeException("不允许变更的目标对象");
        }
    }

}
