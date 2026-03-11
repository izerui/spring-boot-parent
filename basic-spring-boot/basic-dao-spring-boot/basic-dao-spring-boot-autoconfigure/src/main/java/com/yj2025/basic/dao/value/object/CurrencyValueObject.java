package com.yj2025.basic.dao.value.object;

import lombok.Data;

@Data
public class CurrencyValueObject {
    /**
     * 币种编码
     */
    private String currencyCode;
    /**
     * 币种名称
     */
    private String currencyName;
    /**
     * 币种符号
     */
    private String currencySymbol;
}
