package com.yj2025.basic.dao.value.object;

import lombok.Data;

@Data
public class InventoryCustomerValueObject {
    /**
     * 客户货品编码
     */
    private String customerInventoryCode;
    /**
     * 客户货品名称
     */
    private String customerInventoryName;
    /**
     * 客户规格型号
     */
    private String customerInventorySpec;
}
