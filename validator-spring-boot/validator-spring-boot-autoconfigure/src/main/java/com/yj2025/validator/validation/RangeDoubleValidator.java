package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericTypeValidator.formatDouble;
import static org.apache.commons.validator.GenericValidator.isInRange;

/**
 * 范围验证器
 * Created by serv on 2016/12/5.
 */
public class RangeDoubleValidator implements Validator<Double> {

    @Override
    public boolean isValid(Object obj, Property<Double> property) throws ValidatorException {
        String[] vars = property.getVar().split(",");
        return isInRange(property.getValue(),formatDouble(vars[0]),formatDouble(vars[1]));
    }

    @Override
    public String name() {
        return "range";
    }

    @Override
    public Class<Double> pType() {
        return Double.class;
    }
}
