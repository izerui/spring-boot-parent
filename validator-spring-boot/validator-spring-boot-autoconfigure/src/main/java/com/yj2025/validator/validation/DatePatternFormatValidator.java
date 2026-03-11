package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;
import org.apache.commons.validator.routines.TimeValidator;

/**
 * 验证字符是否为指定格式化的日期值
 * Created by serv on 2016/12/5.
 */
public class DatePatternFormatValidator implements Validator<String> {
    @Override
    public boolean isValid(Object obj, Property<String> property) throws ValidatorException {
        return new TimeValidator().isValid(property.getValue(),property.getVar());
    }

    @Override
    public Class<String> pType() {
        return String.class;
    }

    @Override
    public String name() {
        return "date_pattern";
    }
}
