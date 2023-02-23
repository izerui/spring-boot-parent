package com.yj2025.basic.dao.value.object;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class InventoryValueObject {
    @Column(columnDefinition = "VARCHAR(64) COMMENT '货品ID'")
    private String inventoryId;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '货品编码'")
    private String inventoryCode;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '货品名称'")
    private String inventoryName;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '规格型号'")
    private String inventorySpec;
    @Column(columnDefinition = "VARCHAR(64) COMMENT '货品分类'")
    private String inventoryCategoryCode;
    @Column(columnDefinition = "VARCHAR(64) COMMENT '存货类别'")
    private String inventoryType;
    @Column(columnDefinition = "VARCHAR(20) COMMENT '货品属性'")
    private String attributeCode;
    @Column(columnDefinition = "VARCHAR(20) COMMENT '货品单位'")
    private String unitName;
}
