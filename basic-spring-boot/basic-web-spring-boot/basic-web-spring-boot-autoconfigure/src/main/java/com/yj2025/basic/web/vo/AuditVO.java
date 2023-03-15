package com.yj2025.basic.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;
    @Schema(description = "工作流程id")
    private String processInstanceId;

    public AuditVO setAuditName(String auditName) {
        this.auditName = auditName;
        return this;
    }

    public AuditVO() {
    }

    public AuditVO(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public AuditVO(String auditRemark, String auditor, String auditName) {
        this.auditRemark = auditRemark;
        this.auditor = auditor;
        this.auditName = auditName;
        this.auditTime = new Date();
    }

    public AuditVO(String auditRemark, String auditor, String auditName, String processInstanceId) {
        this.auditRemark = auditRemark;
        this.auditor = auditor;
        this.auditName = auditName;
        this.auditTime = new Date();
        this.processInstanceId = processInstanceId;
    }
}
