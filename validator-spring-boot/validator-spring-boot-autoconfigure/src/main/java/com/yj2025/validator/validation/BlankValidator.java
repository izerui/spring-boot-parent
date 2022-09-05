package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericValidator.isBlankOrNull;

/**
 * Created by serv on 2017/5/18.
 */
public class BlankValidator implements Validator<String> {
    @Override
    public boolean isValid(Object obj, Property<String> property) throws ValidatorException {
        return isBlankOrNull(property.getValue());
    }

    @Override
    public String name() {
        return "blank";
    }

    @Override
    public Class<String> pType() {
        return String.class;
    }
}
