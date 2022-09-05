package com.yj2025.validator;

@FunctionalInterface
public interface ValidatorCustomizer {

    /**
     * 个性化验证器
     */
    void customize(ValidatorContextImpl validatorContext);

}
