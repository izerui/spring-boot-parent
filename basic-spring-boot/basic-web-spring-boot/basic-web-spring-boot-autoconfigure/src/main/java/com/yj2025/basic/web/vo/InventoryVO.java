package com.yj2025.basic.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.Assert;

@Data
@Schema(description = "货品信息")
public class InventoryVO {
    @Schema(description = "货品ID")
    private String inventoryId;
    @Schema(description = "货品编码")
    private String inventoryCode;
    @Schema(description = "货品名称")
    private String inventoryName;
    @Schema(description = "规格型号")
    private String inventorySpec;
    @Schema(description = "货品分类CODE")
    private String inventoryCategoryCode;
    @Schema(description = "货品分类名称")
    private String inventoryCategoryName;
    @Schema(description = "存货类别")
    private String inventoryType;
    @Schema(description = "货品属性")
    private String attributeCode;
    @Schema(description = "货品单位")
    private String unitName;

    public String getAttributeName() {
        String attributeName = "";
        switch (this.attributeCode) {
            case "0" -> attributeName = "采购件";
            case "1" -> attributeName = "自制件";
            case "2" -> attributeName = "委外加工件";
            case "3" -> attributeName = "虚拟自制件";
            case "4" -> attributeName = "客供件";
            case "5" -> attributeName = "虚拟件";
            default -> Assert.isTrue(Boolean.FALSE, "不支持的货品属性");
        }
        return attributeName;
    }

    public InventoryVO setInventoryCategoryName(String inventoryCategoryName){
        this.inventoryCategoryName = inventoryCategoryName;
        return this;
    }
}
