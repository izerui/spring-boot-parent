package com.yj2025.basic.dao.value.object;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class AuditValueObject {
    @Column(name = "audit_remark", columnDefinition = "VARCHAR(64) COMMENT '审核/驳回说明'")
    private String auditRemark;
    @Column(name = "auditor", columnDefinition = "VARCHAR(64) COMMENT '审核/驳回人'")
    private String auditor;
    @Column(name = "audit_time", columnDefinition = "DATETIME(3) COMMENT '审核/驳回时间'")
    private String auditTime;
}
