package com.yj2025.validator;

import com.yj2025.validator.validation.Validator;

/**
 * Created by serv on 2016/12/3.
 */
public interface ValidatorContext {

    /**
     * 执行验证
     *
     * @param form                表单名称
     * @param obj                 要验证的对象
     * @param customizeValidators 自定义的验证器
     * @return Results 验证结果
     * @throws ValidatorException
     */
    Results validate(String form, Object obj, Validator<?>... customizeValidators);

    /**
     * 执行验证，并抛出第一个异常
     *
     * @param form                表单名称
     * @param obj                 要验证的对象
     * @param customizeValidators 自定义的验证器
     * @return Results 验证结果
     * @throws ValidatorException
     */
    void validateAndThrowFirst(String form, Object obj, Validator<?>... customizeValidators);


}
