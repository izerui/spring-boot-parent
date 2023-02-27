package com.yj2025.basic.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "币种信息")
public class CurrencyVO {
    @Schema(description = "币种编码")
    private String currencyCode;
    @Schema(description = "币种名称")
    private String currencyName;
    @Schema(description = "币种符号")
    private String currencySymbol;
}
