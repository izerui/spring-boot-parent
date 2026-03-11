
package com.yj2025.validator.parser;

import java.util.List;

/**
 * 验证的字段
 */
public class Field {

    protected String name;
    protected List<ValidatorExec> validators;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ValidatorExec> getValidators() {
        return validators;
    }

    public void setValidators(List<ValidatorExec> validators) {
        this.validators = validators;
    }
}
