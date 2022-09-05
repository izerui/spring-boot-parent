package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;
import org.apache.commons.validator.GenericValidator;

/**
 * 信用卡验证
 * Created by serv on 2016/12/5.
 */
public class CreditCardValidator implements Validator<String> {


    @Override
    public boolean isValid(Object obj, Property<String> property) throws ValidatorException {
        return GenericValidator.isCreditCard(property.getValue());
    }

    @Override
    public String name() {
        return "credit_card";
    }

    @Override
    public Class<String> pType() {
        return String.class;
    }
}
