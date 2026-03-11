package com.yj2025.commons.vo;

import com.google.common.base.MoreObjects;
import com.yj2025.commons.em.MoneyEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TaxRateVO implements Serializable {

    /**
     * 原始数据
     */
    private BigDecimal original;
    /**
     * 是否含税
     */
    private Boolean taxIncluded;

    /**
     * 转换方式，保留几位小数位
     */
    private MoneyEnum moneyEnum;

    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 税金
     */
    private BigDecimal taxes;

    /**
     * 含税数据
     */
    private BigDecimal hasTaxes;

    /**
     * 不含税数据
     */
    private BigDecimal noneTaxes;

    public TaxRateVO(BigDecimal originalValue, boolean taxIncluded, BigDecimal taxRate, MoneyEnum moneyEnum) {
        this.original = originalValue;
        this.hasTaxes = originalValue;
        this.noneTaxes = originalValue;
        this.taxIncluded = taxIncluded;
        this.taxRate = taxRate;
        this.taxes = moneyEnum.format(BigDecimal.ZERO);
        this.moneyEnum = moneyEnum;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("original", original)
                .add("taxIncluded", taxIncluded)
                .add("moneyEnum", moneyEnum)
                .add("taxRate", taxRate)
                .add("taxes", taxes)
                .add("hasTaxes", hasTaxes)
                .add("noneTaxes", noneTaxes)
                .toString();
    }
}
