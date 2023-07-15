package com.yj2025.basic.dao.value.object;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Date;

@Data
@Embeddable
@Deprecated(since = "3.1", forRemoval = true)
public class AuditValueObject {
    @Column(name = "audit_remark", columnDefinition = "VARCHAR(240) COMMENT '审核/驳回说明'")
    private String auditRemark;
    @Column(name = "auditor", columnDefinition = "VARCHAR(64) COMMENT '审核/驳回人CODE'")
    private String auditor;
    @Column(name = "audit_name", columnDefinition = "VARCHAR(64) COMMENT '审核/驳回人名称'")
    private String auditName;
    @Column(name = "audit_time", columnDefinition = "DATETIME(3) COMMENT '审核/驳回时间'")
    private Date auditTime;
    @Column(name = "process_instance_id", columnDefinition = "VARCHAR(64) COMMENT '工作流程id'")
    private String processInstanceId;

    public AuditValueObject(String auditRemark, String auditor, Date auditTime) {
        this.auditRemark = auditRemark;
        this.auditor = auditor;
        this.auditTime = auditTime;
    }

    public AuditValueObject(String auditRemark, String auditor, String auditName) {
        this.auditRemark = auditRemark;
        this.auditor = auditor;
        this.auditName = auditName;
        this.auditTime = new Date();
    }

    public AuditValueObject(String auditRemark, String auditor, String auditName, Date auditTime) {
        this.auditRemark = auditRemark;
        this.auditor = auditor;
        this.auditName = auditName;
        this.auditTime = auditTime;
    }

    public AuditValueObject(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public AuditValueObject() {

    }
}
