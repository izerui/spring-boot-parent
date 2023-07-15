package com.yj2025.basic.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "供应商信息")
@Deprecated(since = "3.1", forRemoval = true)
public class SupplierVO {
    @Schema(description = "供应商CODE")
    private String supplierCode;
    @Schema(description = "供应商名称")
    private String supplierName;
    @Schema(description = "供应商编码")
    private String supplierSerial;
}
