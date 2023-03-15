package com.yj2025.basic.dao.value.object;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 协议类型
 */
public enum AgreementType {
    CASH("现结-款到发货"), MONTH("月结交易"), CASH_ON_DELIVERY("现结-货到付款");

    private final String description;

    AgreementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<AgreementType> of(String description) {
        return Stream.of(AgreementType.values()).filter(agreementType -> agreementType.getDescription().equals(description)).findFirst();
    }
}
