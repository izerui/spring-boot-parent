package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericTypeValidator.formatFloat;
import static org.apache.commons.validator.GenericValidator.isInRange;

/**
 * 范围验证器
 * Created by serv on 2016/12/5.
 */
public class RangeFloatValidator implements Validator<Float> {

    @Override
    public boolean isValid(Object obj, Property<Float> property) throws ValidatorException {
        String[] vars = property.getVar().split(",");
        return isInRange(property.getValue(),formatFloat(vars[0]),formatFloat(vars[1]));
    }

    @Override
    public String name() {
        return "range";
    }

    @Override
    public Class<Float> pType() {
        return Float.class;
    }
}
