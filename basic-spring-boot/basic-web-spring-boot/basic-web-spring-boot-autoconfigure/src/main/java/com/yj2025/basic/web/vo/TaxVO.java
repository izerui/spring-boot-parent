package com.yj2025.basic.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "税率信息")
public class TaxVO {
    @Schema(description = "是否含税")
    private boolean taxIncluded;
    @Schema(description = "税率")
    private BigDecimal taxRate;
}
