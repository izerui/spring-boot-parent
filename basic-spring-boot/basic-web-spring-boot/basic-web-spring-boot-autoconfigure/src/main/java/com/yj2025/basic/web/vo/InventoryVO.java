package com.yj2025.basic.web.vo;

import com.yj2025.basic.dao.value.object.InventoryValueObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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
        if (StringUtils.isBlank(this.attributeCode)) {
            return attributeName;
        }
        switch (this.attributeCode) {
            case "0" -> attributeName = "采购件";
            case "1" -> attributeName = "自制件";
            case "2" -> attributeName = "委外加工件";
            case "3" -> attributeName = "虚拟自制件";
            case "4" -> attributeName = "客供件";
            case "5" -> attributeName = "虚拟件";
        }
        return attributeName;
    }

    public String getInventoryTypeName() {
        String inventoryTypeName = "";
        if (StringUtils.isBlank(this.inventoryType)) {
            return inventoryTypeName;
        }
        switch (this.inventoryType) {
            case "001" -> inventoryTypeName = "成品";
            case "002" -> inventoryTypeName = "半成品";
            case "003" -> inventoryTypeName = "原料";
            case "004" -> inventoryTypeName = "辅料耗材";
            case "005" -> inventoryTypeName = "模治具";
        }
        return inventoryTypeName;
    }

    public InventoryVO setInventoryCategoryName(String inventoryCategoryName) {
        this.inventoryCategoryName = inventoryCategoryName;
        return this;
    }

    public InventoryVO() {
    }

    public InventoryVO(InventoryValueObject inventoryValueObject) {
        this.inventoryId = inventoryValueObject.getInventoryId();
        this.inventoryCode = inventoryValueObject.getInventoryCode();
        this.inventoryName = inventoryValueObject.getInventoryName();
        this.inventorySpec = inventoryValueObject.getInventorySpec();
        this.inventoryCategoryCode = inventoryValueObject.getInventoryCategoryCode();
        this.attributeCode = inventoryValueObject.getAttributeCode();
        this.unitName = inventoryValueObject.getUnitName();
        this.inventoryType = inventoryValueObject.getInventoryType();
    }
}
