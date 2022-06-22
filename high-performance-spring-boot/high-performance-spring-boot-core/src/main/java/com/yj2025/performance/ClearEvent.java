package com.yj2025.performance;

public interface ClearEvent {
    /**
     * 对象重用，需要清除之前设置的一些属性值,跟该对象构造出来的时候保持一致
     */
    void clear();
}
