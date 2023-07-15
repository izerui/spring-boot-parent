package com.yj2025.basic.dao.value.object;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Data
@Embeddable
@Deprecated(since = "3.1", forRemoval = true)
public class SupplierValueObject {
    @Column(columnDefinition = "VARCHAR(64) COMMENT '供应商CODE'")
    private String supplierCode;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '供应商名称'")
    private String supplierName;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '供应商编码'")
    private String supplierSerial;
}
