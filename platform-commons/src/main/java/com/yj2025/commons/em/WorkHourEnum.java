package com.yj2025.commons.em;

import java.math.BigDecimal;

/**
 * Created by serv on 2017/6/22.
 */
public enum WorkHourEnum {

    WORK_HOUR(2);

    private int decimal;

    WorkHourEnum(int decimal) {
        this.decimal = decimal;
    }

    public int getDecimal() {
        return decimal;
    }


    public BigDecimal format(BigDecimal sourceDecimal) {
        if (sourceDecimal == null) {
            return null;
        }
        //向上取整
        return sourceDecimal.setScale(decimal,BigDecimal.ROUND_CEILING).stripTrailingZeros();
    }


}
