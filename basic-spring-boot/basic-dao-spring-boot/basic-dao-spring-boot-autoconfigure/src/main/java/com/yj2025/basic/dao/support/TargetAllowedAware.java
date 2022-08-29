package com.yj2025.basic.dao.support;

import org.springframework.transaction.annotation.Propagation;

import java.util.List;

/**
 * 验证是否允许变更为目标对象, 比如启用、停用、审核改变状态之类的使用
 *
 * @param <T>
 */
public interface TargetAllowedAware<T> {

    List<T> allowedTargets();

    default void validateAllowed(T t) {
        List<T> targets = allowedTargets();
        if (targets != null && !targets.contains(this)) {
            throw new RuntimeException("不允许变更的目标对象");
        }
    }

}
