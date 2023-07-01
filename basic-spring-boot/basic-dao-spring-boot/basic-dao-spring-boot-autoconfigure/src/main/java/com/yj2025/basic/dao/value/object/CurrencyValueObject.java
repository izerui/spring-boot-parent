package com.yj2025.basic.dao.value.object;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Data
@Embeddable
public class CurrencyValueObject {
    @Column(columnDefinition = "VARCHAR(64) COMMENT '币种编码'")
    private String currencyCode;
    @Column(columnDefinition = "VARCHAR(64) COMMENT '币种名称'")
    private String currencyName;
    @Column(columnDefinition = "VARCHAR(64) COMMENT '币种符号'")
    private String currencySymbol;
}
