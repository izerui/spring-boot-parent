package com.yj2025.basic.support;

public interface ApplicationBeanAware {
    /**
     * 获取bean
     *
     * @param beanClass
     * @param <T>
     * @return
     */
    default <T> T $(Class<T> beanClass) {
        return Context.getBean(beanClass);
    }

    /**
     * 获取bean
     *
     * @param beanName
     * @param <T>
     * @return
     */
    default <T> T $(String beanName) {
        return Context.getBean(beanName);
    }
}
