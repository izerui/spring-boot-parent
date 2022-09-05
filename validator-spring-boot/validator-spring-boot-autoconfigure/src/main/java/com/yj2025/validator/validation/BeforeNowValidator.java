package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

import java.util.Date;

/**
 * 验证日期类型的值是否早于当前
 * Created by serv on 2016/12/5.
 */
public class BeforeNowValidator implements Validator<Date> {

    @Override
    public boolean isValid(Object obj, Property<Date> property) throws ValidatorException {
        return property.getValue().before(new Date());
    }

    @Override
    public String name() {
        return "before";
    }

    @Override
    public Class<Date> pType() {
        return Date.class;
    }
}
