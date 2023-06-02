package com.yj2025.cost;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class InventoryPriceVO {
    private String entCode;
    private String inventoryId;
    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public BigDecimal getPrice() {
        return totalAmount.divide(quantity, 8, RoundingMode.HALF_UP);
    }
}
