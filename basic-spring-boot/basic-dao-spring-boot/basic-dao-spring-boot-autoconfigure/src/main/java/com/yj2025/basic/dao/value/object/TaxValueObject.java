package com.yj2025.basic.dao.value.object;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Data
@Embeddable
@Deprecated(since = "3.1", forRemoval = true)
public class TaxValueObject {
    @Column(columnDefinition = "bit(1) DEFAULT b'1' COMMENT '是否含税'")
    private Boolean taxIncluded;
    @Column(columnDefinition = "DECIMAL(5, 2) COMMENT '税率'")
    private BigDecimal taxRate;
}
