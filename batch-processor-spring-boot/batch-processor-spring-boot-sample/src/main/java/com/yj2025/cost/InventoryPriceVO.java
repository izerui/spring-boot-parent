package com.yj2025.cost;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryPriceVO {
    private String entCode;
    private String inventoryId;
    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;

}
