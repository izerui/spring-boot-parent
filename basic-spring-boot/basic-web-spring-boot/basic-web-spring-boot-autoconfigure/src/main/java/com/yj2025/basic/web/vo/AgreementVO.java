package com.yj2025.basic.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@Schema(description = "协议信息")
public class AgreementVO {
    @Schema(description = "协议CODE")
    private String agreementCode;
    @Schema(description = "协议名称'")
    private String agreementName;
    @Schema(description = "协议类型（CASH:现结-款到发货,CASH_ON_DELIVERY:现结-货到付款,MONTH:月结）")
    private String agreementType;
    @Schema(description = "预收百分比")
    private Integer prepareRecPercentage;
    @Schema(description = "发货前百分比")
    private Integer deliverBeforePercentage;
    @Schema(description = "发货后百分比")
    private Integer deliverAfterPercentage;
    @Schema(description = "发货后多少天收付款")
    private Integer deviationDay;

    @Schema(description = "协议类型名称")
    public String getAgreementTypeName() {
        String agreementTypeName = "";
        if (StringUtils.isBlank(this.agreementType)) {
            return agreementTypeName;
        }
        switch (this.agreementType) {
            case "CASH" -> agreementTypeName = "现结-款到发货";
            case "CASH_ON_DELIVERY" -> agreementTypeName = "现结-货到付款";
            case "MONTH" -> agreementTypeName = "月结";
        }
        return agreementTypeName;
    }

    @JsonIgnore
    public boolean isCash() {
        return StringUtils.isNotBlank(this.agreementType) && "CASH".equals(this.agreementType) ? true : false;
    }

    @JsonIgnore
    public boolean isCashOnDelivery() {
        return StringUtils.isNotBlank(this.agreementType) && "CASH_ON_DELIVERY".equals(this.agreementType) ? true : false;
    }

    @JsonIgnore
    public boolean isMonth() {
        return StringUtils.isNotBlank(this.agreementType) && "MONTH".equals(this.agreementType) ? true : false;
    }

    public String getRecAgreementFullName() {
        String agreementFullName = "";
        if (StringUtils.isBlank(agreementType)) {
            return agreementFullName;
        }
        if (isCash()) {
            return getAgreementTypeName() + " " + "预收款" + prepareRecPercentage + " " + "发货前收款" + deliverAfterPercentage + "%";
        }
        if (isCashOnDelivery()) {
            return getAgreementTypeName() + " " + "预收款" + prepareRecPercentage + "%" + " " + "发货后收款" + deliverBeforePercentage + "%" + " " + "发货后" + this.deviationDay + "天后收款";
        }
        return getAgreementTypeName() + " " + "预收款" + prepareRecPercentage + "%" + " " + "发货前收款" + deliverAfterPercentage + "%" + " " + "月结付款周期" + this.deviationDay + "天";
    }

    public String getPayAgreementFullName() {
        return getRecAgreementFullName().replace("收", "付");
    }
}
