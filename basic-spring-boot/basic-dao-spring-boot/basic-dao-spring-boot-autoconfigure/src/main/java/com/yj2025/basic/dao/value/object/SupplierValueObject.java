package com.yj2025.basic.dao.value.object;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class SupplierValueObject {
    @Column(columnDefinition = "VARCHAR(64) COMMENT '供应商CODE'")
    private String supplierCode;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '供应商名称'")
    private String supplierName;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '供应商编码'")
    private String supplierSerial;
}
