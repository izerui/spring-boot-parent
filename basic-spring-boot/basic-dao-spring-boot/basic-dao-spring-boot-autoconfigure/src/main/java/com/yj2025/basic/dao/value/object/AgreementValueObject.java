package com.yj2025.basic.dao.value.object;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Data
@Embeddable
@Deprecated(since = "3.1", forRemoval = true)
public class AgreementValueObject {
    @Column(columnDefinition = "VARCHAR(64) COMMENT '协议CODE'")
    private String agreementCode;
    @Column(columnDefinition = "VARCHAR(64) COMMENT '协议名称'")
    private String agreementName;
    @Column(columnDefinition = "VARCHAR(64) COMMENT '协议类型（CASH:现结-款到发货,CASH_ON_DELIVERY:现结-货到付款,MONTH:月结）'")
    private String agreementType;
    @Column(columnDefinition = "VARCHAR(64) COMMENT '预收百分比'")
    private Integer prepareRecPercentage;
    @Column(columnDefinition = "VARCHAR(64) COMMENT '发货前百分比'")
    private Integer deliverAfterPercentage;
    @Column(columnDefinition = "VARCHAR(64) COMMENT '发货后百分比'")
    private Integer deliverBeforePercentage;
    @Column(columnDefinition = "VARCHAR(64) COMMENT '发货后多少天收付款'")
    private Integer deviationDay;

    public boolean isCash() {
        return StringUtils.isNotBlank(this.agreementType) && "CASH".equals(this.agreementType) ? true : false;
    }

    public boolean isCashOnDelivery() {
        return StringUtils.isNotBlank(this.agreementType) && "CASH_ON_DELIVERY".equals(this.agreementType) ? true : false;
    }

    public boolean isMonth() {
        return StringUtils.isNotBlank(this.agreementType) && "MONTH".equals(this.agreementType) ? true : false;
    }
}
