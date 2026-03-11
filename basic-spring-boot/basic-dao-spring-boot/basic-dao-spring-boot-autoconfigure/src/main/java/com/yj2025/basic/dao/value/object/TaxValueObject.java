package com.yj2025.basic.dao.value.object;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaxValueObject {
    /**
     * 是否含税
     */
    private Boolean taxIncluded;
    /**
     * 税率
     */
    private BigDecimal taxRate;
}
