package com.yj2025.basic.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class BasicAuthDTO implements Serializable {

    protected String entCode;
    protected String entName;
    protected String userCode;
    protected String userName;
    protected String accountCode;
    protected String accountName;
}
