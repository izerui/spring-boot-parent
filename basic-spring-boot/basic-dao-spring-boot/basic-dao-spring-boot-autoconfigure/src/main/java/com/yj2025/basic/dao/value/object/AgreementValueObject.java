package com.yj2025.basic.dao.value.object;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Data
public class AgreementValueObject {
    /**
     * 协议CODE
     */
    private String agreementCode;
    /**
     * 协议名称
     */
    private String agreementName;
    /**
     * 协议类型（CASH:现结-款到发货,CASH_ON_DELIVERY:现结-货到付款,MONTH:月结）
     */
    private String agreementType;
    /**
     * 预收百分比
     */
    private BigDecimal prepareRecPercentage;
    /**
     * 发货后百分比
     */
    private BigDecimal deliverAfterPercentage;
    /**
     * 协议CODE
     */
    private BigDecimal deliverBeforePercentage;
    /**
     * 发货后多少天收付款
     */
    private Integer deviationDay;

    public boolean isCash() {
        return StringUtils.isNotBlank(this.agreementType) && "CASH".equals(this.agreementType);
    }

    public boolean isCashOnDelivery() {
        return StringUtils.isNotBlank(this.agreementType) && "CASH_ON_DELIVERY".equals(this.agreementType);
    }

    public boolean isMonth() {
        return StringUtils.isNotBlank(this.agreementType) && "MONTH".equals(this.agreementType);
    }
}
