package com.yj2025.validator.validation;

import com.yj2025.validator.ValidatorException;

/**
 * Created by serv on 2016/12/6.
 */
public class Property<T> {

    /**
     * 所属验证的表单名称
     */
    private String form;
    /**
     * 属性的名称
     */
    private String name;
    /**
     * 属性对应的 readMethod 获取的值
     */
    private T value;

    /**
     * 属性对应的类型
     */
    private Class<T> type;

    /**
     * form表单field中配置的变量
     */
    private String var;

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public String getVar() throws ValidatorException {
        if(var==null||var.equals("")){
            throw new ValidatorException(name+" 缺少变量");
        }
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }
}
