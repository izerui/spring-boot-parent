package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericTypeValidator.formatInt;
import static org.apache.commons.validator.GenericValidator.isInRange;

/**
 * 范围验证器
 * Created by serv on 2016/12/5.
 */
public class RangeIntValidator implements Validator<Integer> {

    @Override
    public boolean isValid(Object obj, Property<Integer> property) throws ValidatorException {
        String[] vars = property.getVar().split(",");
        return isInRange(property.getValue(),formatInt(vars[0]),formatInt(vars[1]));
    }

    @Override
    public String name() {
        return "range";
    }

    @Override
    public Class<Integer> pType() {
        return Integer.class;
    }
}
