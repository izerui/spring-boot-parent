package com.yj2025.basic.dao.value.object;

import lombok.Data;

@Data
public class InventorySupplierValueObject {
    /**
     * 供应商货品编码
     */
    private String supplierInventoryCode;
    /**
     * 供应商货品名称
     */
    private String supplierInventoryName;
    /**
     * 供应商规格型号
     */
    private String supplierInventorySpec;
}
