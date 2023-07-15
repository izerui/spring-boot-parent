package com.yj2025.basic.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "客户料号信息")
@Deprecated(since = "3.1", forRemoval = true)
public class InventoryCustomerVO {
    @Schema(description = "客户货品编码")
    private String customerInventoryCode;
    @Schema(description = "客户货品名称")
    private String customerInventoryName;
    @Schema(description = "客户规格型号")
    private String customerInventorySpec;
}
