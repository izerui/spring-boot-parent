package com.yj2025.commons.em;

import java.math.BigDecimal;

import static com.yj2025.commons.util.BigDecimalUtils.multiply;

/**
 * Created by serv on 2017/6/22.
 */
public enum LossRateEnum {

    LOSS_RATE(6);

    private int decimal;

    LossRateEnum(int decimal) {
        this.decimal = decimal;
    }

    public int getDecimal() {
        return decimal;
    }


    public BigDecimal format(BigDecimal lossRate) {
        if (lossRate == null) {
            return BigDecimal.ZERO;
        }
        //向上取整
        return lossRate.setScale(decimal, BigDecimal.ROUND_CEILING).stripTrailingZeros();
    }

    /**
     * 损耗数量
     *
     * @param virtualQuantity 用量
     * @param lossRate        损耗率
     * @return
     */
    public BigDecimal lossRateQuantity(BigDecimal virtualQuantity, BigDecimal lossRate) {
        // (用量 * 损耗率) /100
        return multiply(format(lossRate), virtualQuantity).divide(new BigDecimal("100"));
    }


}
