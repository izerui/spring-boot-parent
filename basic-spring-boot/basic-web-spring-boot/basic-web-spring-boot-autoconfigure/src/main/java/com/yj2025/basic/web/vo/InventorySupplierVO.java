package com.yj2025.basic.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Schema(description = "供应商料号信息")
public class InventorySupplierVO {
    @Schema(description = "供应商货品编码")
    private String supplierInventoryCode;
    @Schema(description = "供应商货品名称")
    private String supplierInventoryName;
    @Schema(description = "供应商规格型号")
    private String supplierInventorySpec;
}
