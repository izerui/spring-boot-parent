package com.yj2025.basic.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Data
@Schema(description = "客户信息")
public class CustomerVO {
    @Schema(description = "客户CODE")
    private String customerCode;
    @Schema(description = "客户名称")
    private String customerName;
    @Schema(description = "客户编码")
    private String customerSerial;
}
