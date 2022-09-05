package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericTypeValidator.formatLong;
import static org.apache.commons.validator.GenericValidator.isInRange;

/**
 * 范围验证器
 * Created by serv on 2016/12/5.
 */
public class RangeLongValidator implements Validator<Long> {

    @Override
    public boolean isValid(Object obj, Property<Long> property) throws ValidatorException {
        String[] vars = property.getVar().split(",");
        return isInRange(property.getValue(),formatLong(vars[0]),formatLong(vars[1]));
    }

    @Override
    public String name() {
        return "range";
    }

    @Override
    public Class<Long> pType() {
        return Long.class;
    }
}
