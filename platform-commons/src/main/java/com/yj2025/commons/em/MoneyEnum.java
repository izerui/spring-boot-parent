package com.yj2025.commons.em;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by serv on 2017/6/22.
 */
public enum MoneyEnum {

    /**
     * 金额
     */
    AMOUNT(2, "#,###.##"),
    /**
     * 价格
     */
    PRICE(8, "#,###.########"),
    /**
     * 税率
     */
    TAX_RATE(2, "#.##"),
    /**
     * 汇率
     */
    EXCHANGE_RATE(6, "#,###.######"),

    UNIT_DECIMALS(5, "#,###.#####"),

    PRICING_VALUE_DECIMALS(5, "#,###.#####"),
    HOUR(5, "#.#####"),
    STATEMENT_AMOUNT(4, "#,###.####"),
    NONE(0, "#.#");

    private int decimal;

    private String format;

    MoneyEnum(int decimal, String format) {
        this.decimal = decimal;
        this.format = format;
    }

    public int getDecimal() {
        return decimal;
    }

    public String getFormatContent() {
        return this.format;
    }

    public BigDecimal format(BigDecimal sourceDecimal) {
        if (sourceDecimal == null) {
            return null;
        }
        return sourceDecimal.setScale(decimal, BigDecimal.ROUND_HALF_UP);

    }

    public String formatBigDecimal(BigDecimal sourceDecimal) {
        if (sourceDecimal == null) {
            return null;
        }
        return new DecimalFormat(this.format).format(format(sourceDecimal));
    }

}
