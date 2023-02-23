package com.yj2025.basic.dao.value.object;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Data
@Embeddable
public class TaxValueObject {
    @Column(columnDefinition = "bit(1) COMMENT '是否含税'")
    private boolean taxIncluded = true;
    @Column(columnDefinition = "DECIMAL(5, 2) COMMENT '税率'")
    private BigDecimal taxRate;
}
