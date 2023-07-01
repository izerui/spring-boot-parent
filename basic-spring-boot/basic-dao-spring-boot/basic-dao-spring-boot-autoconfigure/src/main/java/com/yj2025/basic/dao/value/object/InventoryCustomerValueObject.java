package com.yj2025.basic.dao.value.object;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Data
@Embeddable
public class InventoryCustomerValueObject {
    @Column(columnDefinition = "VARCHAR(240) COMMENT '客户货品编码'")
    private String customerInventoryCode;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '客户货品名称'")
    private String customerInventoryName;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '客户规格型号'")
    private String customerInventorySpec;
}
