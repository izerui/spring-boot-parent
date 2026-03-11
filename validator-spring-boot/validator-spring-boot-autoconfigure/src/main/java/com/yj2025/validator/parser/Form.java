
package com.yj2025.validator.parser;

import java.util.List;

/**
 * 验证的表单
 */
public class Form {

    protected String name;
    protected String description;
    protected List<Field> fields;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
