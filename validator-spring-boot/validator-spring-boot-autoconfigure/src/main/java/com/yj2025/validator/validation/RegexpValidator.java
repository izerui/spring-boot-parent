package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericValidator.matchRegexp;

/**
 * 正则表达式验证
 * Created by serv on 2016/12/5.
 */
public class RegexpValidator implements Validator<String> {

    @Override
    public boolean isValid(Object obj, Property<String> property) throws ValidatorException {
        return matchRegexp(property.getValue(),property.getVar());
    }

    @Override
    public String name() {
        return "regexp";
    }

    @Override
    public Class<String> pType() {
        return String.class;
    }
}
