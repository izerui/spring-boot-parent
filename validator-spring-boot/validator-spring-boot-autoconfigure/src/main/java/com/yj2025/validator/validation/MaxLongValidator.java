package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericTypeValidator.formatLong;
import static org.apache.commons.validator.GenericValidator.maxValue;

/**
 * long最大验证器
 * Created by serv on 2016/12/6.
 */
public class MaxLongValidator implements Validator<Long> {
    @Override
    public boolean isValid(Object obj, Property<Long> property) throws ValidatorException {
        return maxValue(property.getValue(), formatLong(property.getVar()));
    }

    @Override
    public String name() {
        return "max";
    }

    @Override
    public Class<Long> pType() {
        return Long.class;
    }
}
