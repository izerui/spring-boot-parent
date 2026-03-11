package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericTypeValidator.formatFloat;
import static org.apache.commons.validator.GenericValidator.minValue;

/**
 * float最小验证器
 * Created by serv on 2016/12/6.
 */
public class MinFloatValidator implements Validator<Float> {
    @Override
    public boolean isValid(Object obj, Property<Float> property) throws ValidatorException {
        return minValue(property.getValue(),formatFloat(property.getVar()));
    }

    @Override
    public String name() {
        return "min";
    }

    @Override
    public Class<Float> pType() {
        return Float.class;
    }
}
