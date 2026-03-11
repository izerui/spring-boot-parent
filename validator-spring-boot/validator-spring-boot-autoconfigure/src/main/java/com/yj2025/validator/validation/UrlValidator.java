package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import static org.apache.commons.validator.GenericValidator.isUrl;

/**
 * url验证器
 * Created by serv on 2016/12/5.
 */
public class UrlValidator implements Validator<String> {


    @Override
    public boolean isValid(Object obj, Property<String> property) throws ValidatorException {
        return isUrl(property.getValue());
    }

    @Override
    public String name() {
        return "url";
    }

    @Override
    public Class<String> pType() {
        return String.class;
    }
}
