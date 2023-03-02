package com.yj2025.commons.util;


import com.yj2025.commons.em.LossRateEnum;
import com.yj2025.commons.em.UnitEnum;

import java.math.BigDecimal;

import static com.yj2025.commons.util.BigDecimalUtils.*;

/**
 * Created by serv on 2017/6/19.
 */
public class BusinessQuantity {

    private String unitName;

    private BusinessQuantity() {
    }

    private BusinessQuantity(String unitName) {
        this.unitName = unitName;
    }

    public static BusinessQuantity use(String unitName) {
        return new BusinessQuantity(unitName);
    }

    /**
     * 按计量单位格式化
     *
     * @return
     */
    @Deprecated
    public BigDecimal format(BigDecimal decimal) {
        return UnitEnum.instanceOf(unitName).formatUp(decimal);
    }

    /**
     * 向上取整
     *
     * @return
     */
    public BigDecimal formatUp(BigDecimal decimal) {
        return UnitEnum.instanceOf(unitName).formatUp(decimal);
    }

    /**
     * 银行家舍入
     *
     * @return
     */
    public BigDecimal formatEven(BigDecimal decimal) {
        return UnitEnum.instanceOf(unitName).formatEven(decimal);
    }

    /**
     * 向下取整
     *
     * @return
     */
    public BigDecimal formatDown(BigDecimal decimal) {
        return UnitEnum.instanceOf(unitName).formatDown(decimal);
    }

    /**
     * 格式化产品用量
     *
     * @param parentVirtualQuantity 上级货品的虚拟用量(用来计算当前数量的一个基准)
     * @param quantity              单位用量
     * @return
     */
    public BigDecimal formatProductQuantity(BigDecimal parentVirtualQuantity, BigDecimal quantity) {

        //产品用量
        BigDecimal _productQuantity = multiply(quantity, parentVirtualQuantity);

        return formatUp(_productQuantity);
    }


    /**
     * 格式化损耗数量
     *
     * @param parentVirtualQuantity 上级货品的虚拟用量(用来计算当前数量的一个基准)
     * @param quantity              单位用量
     * @param lossRate              损耗率
     * @return
     */
    public BigDecimal formatLossRateQuantity(BigDecimal parentVirtualQuantity, BigDecimal quantity, BigDecimal lossRate) {

        // 生产用量
        BigDecimal productionQuantity = formatProductionQuantity(parentVirtualQuantity, quantity, lossRate);

        // 产品用量
        BigDecimal productQuantity = formatProductQuantity(parentVirtualQuantity, quantity);

        // 生产用量 - 产品用量 = 损耗数量
        return formatUp(productionQuantity.subtract(productQuantity));
    }


    /**
     * 格式化生产用量
     *
     * @param parentVirtualQuantity 上级货品的虚拟用量(用来计算当前数量的一个基准)
     * @param quantity              单位用量
     * @param lossRate              损耗率
     * @return
     */
    public BigDecimal formatProductionQuantity(BigDecimal parentVirtualQuantity, BigDecimal quantity, BigDecimal lossRate) {

        lossRate = LossRateEnum.LOSS_RATE.format(lossRate);

        //产品用量
        BigDecimal _productQuantity = multiply(quantity, parentVirtualQuantity);

        //损耗数量
        BigDecimal _lossRateQuantity = multiply(lossRate, _productQuantity).divide(new BigDecimal("100"));

        //生产用量
        BigDecimal _productionQuantity = add(_productQuantity, _lossRateQuantity);

        //生产用量
        return formatUp(_productionQuantity);
    }

    /**
     * 格式化生产用量
     *
     * @param parentVirtualQuantity 上级货品的虚拟用量(用来计算当前数量的一个基准)
     * @param quantity              单位用量
     * @param lossRate              损耗率
     * @param fixedLossQuantity     固定损耗
     * @return
     */
    public BigDecimal formatProductionQuantity(BigDecimal parentVirtualQuantity, BigDecimal quantity, BigDecimal lossRate, BigDecimal fixedLossQuantity) {
        //生产用量
        BigDecimal _productionQuantity = formatProductionQuantity(parentVirtualQuantity, quantity, lossRate);
        if (isGreaterThan(_productionQuantity, BigDecimal.ZERO)) {
            _productionQuantity = _productionQuantity.add(fixedLossQuantity);
        }
        //生产用量
        return formatUp(_productionQuantity);
    }

    /**
     * 格式化生产用量
     *
     * @param parentVirtualQuantity 上级货品的虚拟用量(用来计算当前数量的一个基准)
     * @param quantity              单位用量
     * @param lossRate              损耗率
     * @return
     */
    public BigDecimal formatDownProductionQuantity(BigDecimal parentVirtualQuantity, BigDecimal quantity, BigDecimal lossRate) {

        lossRate = LossRateEnum.LOSS_RATE.format(lossRate);

        //产品用量
        BigDecimal _productQuantity = multiply(quantity, parentVirtualQuantity);

        //损耗数量
        BigDecimal _lossRateQuantity = multiply(lossRate, _productQuantity).divide(new BigDecimal("100"));

        //生产用量
        BigDecimal _productionQuantity = add(_productQuantity, _lossRateQuantity);

        //生产用量
        return formatDown(_productionQuantity);
    }

    /**
     * 考虑损耗的基础上计算齐套数量 注意单位应该使用齐套数所对应的单位
     *
     * @param childQuantity 子件的单位用量
     * @param childUsable   子件的可用量
     * @param childLossRate 子件的损耗率
     * @return 格式化后的齐套数量
     */
    public BigDecimal formatHomogQty(BigDecimal childQuantity, BigDecimal childUsable, BigDecimal childLossRate) {
        // 单位损耗数量
        BigDecimal _childRateQty = add(BigDecimal.ONE, childLossRate.divide(new BigDecimal(100), 8, BigDecimal.ROUND_FLOOR));

        // (下层齐套数量 + 可用量) 除以 单位损耗量 再 除以 单位用量 向下取整 得出齐套数量
        BigDecimal result = childUsable
                .divide(_childRateQty.multiply(childQuantity), 8, BigDecimal.ROUND_FLOOR);

        return UnitEnum.instanceOf(unitName).formatDown(result);
    }

    /**
     * 不考虑损耗的基础上计算齐套数量 注意单位应该使用齐套数所对应的单位
     *
     * @param childQuantity 子件的单位用量
     * @param childUsable   子件的可用量
     * @return 格式化后的齐套数量
     */
    public BigDecimal formatHomogQty(BigDecimal childQuantity, BigDecimal childUsable) {
        // (下层齐套数量 + 可用量) 除以 单位用量 向下取整 得出齐套数量
        BigDecimal result = childUsable
                .divide(childQuantity, 8, BigDecimal.ROUND_FLOOR);

        return UnitEnum.instanceOf(unitName).formatDown(result);
    }


    /**
     * 加法
     *
     * @param num1
     * @param num2
     * @return
     */
    public BigDecimal formatAdd(BigDecimal num1, BigDecimal num2) {
        return formatUp(add(num1, num2));
    }

    /**
     * 减法
     *
     * @param num1
     * @param num2
     * @return
     */
    public BigDecimal formatSubstract(BigDecimal num1, BigDecimal num2) {
        return formatUp(substract(num1, num2));
    }

    /**
     * 乘法
     *
     * @param num1
     * @param num2
     * @return
     */
    public BigDecimal formatMultiply(BigDecimal num1, BigDecimal num2) {
        return formatUp(multiply(num1, num2));
    }

    /**
     * 乘法向下取整
     *
     * @param num1
     * @param num2
     * @return
     */
    public BigDecimal formatMultiplyDown(BigDecimal num1, BigDecimal num2) {
        return formatDown(multiply(num1, num2));
    }


    /**
     * 除法
     *
     * @param num1
     * @param num2
     * @return
     */
    public BigDecimal formatDivide(BigDecimal num1, BigDecimal num2) {
        return formatUp(divide(num1, num2, 8));
    }

    /**
     * 除法 向下取整
     *
     * @param num1
     * @param num2
     * @return
     */
    public BigDecimal formatDivideDown(BigDecimal num1, BigDecimal num2) {
        return formatDown(divide(num1, num2, 8));
    }
}
