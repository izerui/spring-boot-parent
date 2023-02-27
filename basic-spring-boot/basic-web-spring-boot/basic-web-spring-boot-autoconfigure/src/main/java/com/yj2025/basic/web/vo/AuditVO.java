package com.yj2025.basic.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "审核信息")
public class AuditVO {
    @Schema(description = "审核/驳回说明")
    private String auditRemark;
    @Schema(description = "审核/驳回人ID")
    private String auditor;
    @Schema(description = "审核/驳回人名称")
    private String auditName;
    @Schema(description = "审核/驳回时间")
    private String auditTime;
    @Schema(description = "工作流程id")
    private String processInstanceId;

    public AuditVO setAuditName(String auditName) {
        this.auditName = auditName;
        return this;
    }
}
