package com.yj2025.cost;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomeMadeFinishedPO {

    private Long id;
    private String entCode;
    private String demandId;
    private String inventoryId;
    private Integer ym;
    private String attributeCode;
    private BigDecimal quantity;
    private BigDecimal productionQuantity;
}
