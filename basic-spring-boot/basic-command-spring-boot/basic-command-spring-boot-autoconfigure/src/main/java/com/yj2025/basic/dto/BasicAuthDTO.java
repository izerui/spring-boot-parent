package com.yj2025.basic.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Validated
public abstract class BasicAuthDTO implements Serializable {

    @NotBlank(message = "账套编号不能为空")
    protected String entCode;

    protected String entName;

    @NotBlank(message = "用户编号不能为空")
    protected String userCode;

    protected String userName;

    protected String accountCode;

    protected String accountName;

}
