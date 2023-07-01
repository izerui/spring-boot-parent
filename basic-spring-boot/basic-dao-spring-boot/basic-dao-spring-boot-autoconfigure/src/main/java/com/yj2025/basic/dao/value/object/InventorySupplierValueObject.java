package com.yj2025.basic.dao.value.object;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Data
@Embeddable
public class InventorySupplierValueObject {
    @Column(columnDefinition = "VARCHAR(240) COMMENT '供应商货品编码'")
    private String supplierInventoryCode;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '供应商货品名称'")
    private String supplierInventoryName;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '供应商规格型号'")
    private String supplierInventorySpec;
}
