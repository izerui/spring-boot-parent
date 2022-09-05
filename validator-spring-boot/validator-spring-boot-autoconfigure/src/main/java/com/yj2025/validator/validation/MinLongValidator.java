package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericTypeValidator.formatLong;
import static org.apache.commons.validator.GenericValidator.minValue;

/**
 * long最小验证器
 * Created by serv on 2016/12/6.
 */
public class MinLongValidator implements Validator<Long> {
    @Override
    public boolean isValid(Object obj, Property<Long> property) throws ValidatorException {
        return minValue(property.getValue(), formatLong(property.getVar()));
    }

    @Override
    public String name() {
        return "min";
    }

    @Override
    public Class<Long> pType() {
        return Long.class;
    }
}
