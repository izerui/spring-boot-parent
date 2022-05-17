package com.yj2025.websocket;

import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ToString
public class WebMsg implements Serializable {

    private String entCode;
    private String userCode;
    private Map<String, String> message = new HashMap<>();

    public WebMsg() {
    }

    public WebMsg(String entCode, String userCode) {
        this.entCode = entCode;
        this.userCode = userCode;
    }

    public WebMsg(String entCode, String userCode, String type) {
        this.entCode = entCode;
        this.userCode = userCode;
        message.put("type", type);
    }

    public String getEntCode() {
        return entCode;
    }

    public void setEntCode(String entCode) {
        this.entCode = entCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Map<String, String> getMessage() {
        return message;
    }

    public WebMsg set(String key, String value) {
        message.put(key, value);
        return this;
    }

}
