package com.yj2025.validator;

/**
 * Created by serv on 2016/12/3.
 */
public class Result {

    /**
     * 属性名
     */
    private String property;
    /**
     * 是否验证有效
     */
    private boolean valid;
    /**
     * 无效的情况下的提示信息
     */
    private String msg;

    public String getProperty() {
        return property;
    }

    void setProperty(String property) {
        this.property = property;
    }

    public boolean isValid() {
        return valid;
    }

    void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMsg() {
        return msg;
    }

    void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "{preoperty:\""+property+"\",valid:"+valid+",msg:\""+msg+"\"}";
    }
}
