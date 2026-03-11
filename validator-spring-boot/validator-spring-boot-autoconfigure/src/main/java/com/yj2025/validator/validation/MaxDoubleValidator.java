package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericTypeValidator.formatDouble;
import static org.apache.commons.validator.GenericValidator.minValue;

/**
 * double最大验证器
 * Created by serv on 2016/12/5.
 */
public class MaxDoubleValidator implements Validator<Double> {
    @Override
    public boolean isValid(Object obj, Property<Double> property) throws ValidatorException {
        return minValue(property.getValue(),formatDouble(property.getVar()));
    }

    @Override
    public Class<Double> pType() {
        return Double.class;
    }

    @Override
    public String name() {
        return "max";
    }
}
