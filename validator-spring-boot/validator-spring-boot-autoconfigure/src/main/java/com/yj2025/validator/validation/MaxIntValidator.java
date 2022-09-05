package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericTypeValidator.formatInt;
import static org.apache.commons.validator.GenericValidator.maxValue;

/**
 * int最大验证器
 * Created by serv on 2016/12/6.
 */
public class MaxIntValidator implements Validator<Integer> {
    @Override
    public boolean isValid(Object obj, Property<Integer> property) throws ValidatorException {
        return maxValue(property.getValue(),formatInt(property.getVar()));
    }

    @Override
    public String name() {
        return "max";
    }

    @Override
    public Class<Integer> pType() {
        return Integer.class;
    }
}
