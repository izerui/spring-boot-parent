package com.yj2025.basic.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "审核信息")
public class AuditVO {
    @Schema(description = "审核/驳回说明")
    private String auditRemark;
    @Schema(description = "审核/驳回人")
    private String auditor;
    @Schema(description = "审核/驳回时间")
    private String auditTime;
}
