package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericTypeValidator.formatLong;
import static org.apache.commons.validator.GenericTypeValidator.formatShort;
import static org.apache.commons.validator.GenericValidator.isInRange;

/**
 * 范围验证器
 * Created by serv on 2016/12/5.
 */
public class RangeShortValidator implements Validator<Short> {

    @Override
    public boolean isValid(Object obj, Property<Short> property) throws ValidatorException {
        String[] vars = property.getVar().split(",");
        return isInRange(property.getValue(),formatShort(vars[0]),formatLong(vars[1]));
    }

    @Override
    public String name() {
        return "range";
    }

    @Override
    public Class<Short> pType() {
        return Short.class;
    }
}
