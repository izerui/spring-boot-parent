package com.yj2025.commons.jackson;

import java.math.BigDecimal;

public class Decimal2StringUtils {

    public static String toPlainString(BigDecimal decimal) {
        String decimalStr = decimal != null ? decimal.toPlainString() : null;
        if (decimalStr != null && decimalStr.indexOf(".") > 0) {
            decimalStr = decimalStr.replaceAll("0+?$", "");
            decimalStr = decimalStr.replaceAll("[.]$", "");
        }
        return decimalStr;
    }
}
