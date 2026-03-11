package com.yj2025.websocket;

import com.yj2025.websocket.producer.WebSocketContext;
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

    public WebMsg setEntCode(String entCode) {
        this.entCode = entCode;
        return this;
    }

    public String getUserCode() {
        return userCode;
    }

    public WebMsg setUserCode(String userCode) {
        this.userCode = userCode;
        return this;
    }

    public Map<String, String> getMessage() {
        return message;
    }

    public WebMsg set(String key, String value) {
        message.put(key, value);
        return this;
    }

    public void send(WebSocketContext context) {
        context.sendMessage(this);
    }

}
