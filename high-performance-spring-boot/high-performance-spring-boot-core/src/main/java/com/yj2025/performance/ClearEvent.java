package com.yj2025.performance;

public interface ClearEvent {
    /**
     * 对象重用，需要清除之前设置的一些属性值
     */
    void clear();
}
