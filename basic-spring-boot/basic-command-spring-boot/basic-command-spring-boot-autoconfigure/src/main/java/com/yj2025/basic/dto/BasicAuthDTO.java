package com.yj2025.basic.dto;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
public abstract class BasicAuthDTO implements Serializable {

    @NonNull
    private Auth auth;

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
