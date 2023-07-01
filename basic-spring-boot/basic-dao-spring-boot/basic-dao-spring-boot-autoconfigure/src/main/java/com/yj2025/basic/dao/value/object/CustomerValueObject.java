package com.yj2025.basic.dao.value.object;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Data
@Embeddable
public class CustomerValueObject {
    @Column(columnDefinition = "VARCHAR(64) COMMENT '客户CODE'")
    private String customerCode;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '客户名称'")
    private String customerName;
    @Column(columnDefinition = "VARCHAR(240) COMMENT '客户编码'")
    private String customerSerial;
}
