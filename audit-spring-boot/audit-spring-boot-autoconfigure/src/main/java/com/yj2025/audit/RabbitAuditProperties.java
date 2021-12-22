package com.yj2025.audit;

import lombok.Data;

@Data
public class RabbitAuditProperties {

    private String exchange = "ierp";
    private String routingKey = "audit";
}
