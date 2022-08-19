package com.yj2025.basic.dto;

import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;

public abstract class BasicAuthDTO implements Serializable {

    private Auth auth;

    public Auth getAuth() {
        Assert.notNull(auth, "无法获取auth对象,请检查是否赋值");
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    @Data
    public static class Auth {
        private String entCode;
        private String entName;
        private String userCode;
        private String userName;
        private String accountCode;
        private String accountName;
    }
}
