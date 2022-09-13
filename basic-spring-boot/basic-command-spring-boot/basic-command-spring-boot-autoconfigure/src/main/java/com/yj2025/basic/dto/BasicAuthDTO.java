package com.yj2025.basic.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Validated
public abstract class BasicAuthDTO implements Serializable {

    @NotBlank(message = "账套编号不能为空")
    protected String entCode;

    @NotBlank(message = "账套名称不能为空")
    protected String entName;

    @NotBlank(message = "用户编号不能为空")
    protected String userCode;

    @NotBlank(message = "用户名不能为空")
    protected String userName;

    @NotBlank(message = "账号编码不能为空")
    protected String accountCode;

    @NotBlank(message = "账号名不能为空")
    protected String accountName;

}
