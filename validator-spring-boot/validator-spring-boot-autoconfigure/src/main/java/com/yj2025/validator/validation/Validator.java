package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

/**
 * Created by serv on 2016/12/2.
 */
public interface Validator<T> {

    /**
     * 验证执行方法
     * @param obj 要验证的对象
     * @param property 要验证的属性
     * @return
     */
    boolean isValid(Object obj, Property<T> property) throws ValidatorException;

    /**
     * 该验证器的名字 被exec使用,必须返回一个固定的名字
     * @return
     */
    String name();

    /**
     * 要验证的属性的类型
     * @return
     */
    Class<T> pType();

}
