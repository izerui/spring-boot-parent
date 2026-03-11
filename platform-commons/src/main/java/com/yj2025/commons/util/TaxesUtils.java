package com.yj2025.commons.util;

import com.yj2025.commons.em.MoneyEnum;
import com.yj2025.commons.vo.TaxRateVO;

import java.math.BigDecimal;

public class TaxesUtils {

    public TaxesUtils() {
    }

    /**
     * 计算价税信息，
     *
     * @param moneyEnum     格式化方式
     * @param originalValue 原始金额
     * @param taxIncluded   是否含税
     * @param taxRate       税率
     * @return
     */
    public static TaxRateVO calcTaxes(MoneyEnum moneyEnum, BigDecimal originalValue, Boolean taxIncluded, BigDecimal taxRate) {
        taxRate = MoneyEnum.TAX_RATE.format(BigDecimalUtils.null2Zero(taxRate));
        originalValue = moneyEnum.format(BigDecimalUtils.null2Zero(originalValue));
        int scale = moneyEnum.getDecimal();
        TaxRateVO vo = new TaxRateVO(originalValue, taxIncluded, taxRate, moneyEnum);
        if (BigDecimalUtils.eqZero(originalValue) || BigDecimalUtils.eqZero(taxRate) || taxIncluded == null) {
            return vo;
        }
        if (taxIncluded) {
            //含税算税金
            //税金 = 总价-（总价÷（1+税率/100））
            //税金 = 总价-（总价*100÷（100+税率）/100）
//            BigDecimal noneTaxes = moneyEnum.format(originalValue.divide(BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), scale, BigDecimal.ROUND_HALF_UP)), scale, BigDecimal.ROUND_HALF_UP));
            BigDecimal noneTaxes = originalValue.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(taxRate), scale, BigDecimal.ROUND_HALF_UP);
            BigDecimal taxes = originalValue.subtract(noneTaxes);
            vo.setTaxes(taxes);
            vo.setHasTaxes(moneyEnum.format(originalValue));
            vo.setNoneTaxes(moneyEnum.format(noneTaxes));
        } else {
            //不含税算税金
            BigDecimal taxes = moneyEnum.format(originalValue.multiply(taxRate.divide(BigDecimal.valueOf(100), scale, BigDecimal.ROUND_HALF_UP)));
            vo.setTaxes(taxes);
            vo.setHasTaxes(moneyEnum.format(originalValue.add(taxes)));
            vo.setNoneTaxes(moneyEnum.format(originalValue));
        }
        return vo;
    }

    public static void main(String[] args) {
        TaxRateVO taxRateVo = TaxesUtils.calcTaxes(MoneyEnum.AMOUNT, new BigDecimal(100), true, new BigDecimal(6.5));
        System.out.println(taxRateVo);
    }
}
