package com.yj2025.basic.dao.value.object;

import lombok.Data;

import java.util.Date;

@Data
public class AuditValueObject {
    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 审核人CODE
     */
    private String auditor;
    /**
     * 审核人名称
     */
    private String auditName;
    /**
     * 审核时间
     */
    private Date auditTime;
    /**
     * 关联的审批流的实例ID
     */
    private String processInstanceId;

    public static AuditValueObject of(String auditRemark, String auditor, Date auditTime) {
        AuditValueObject valueObject = new AuditValueObject();
        valueObject.setAuditRemark(auditRemark);
        valueObject.setAuditor(auditor);
        valueObject.setAuditTime(auditTime);
        return valueObject;
    }

    public static AuditValueObject of(String auditRemark, String auditor, String auditName) {
        AuditValueObject valueObject = new AuditValueObject();
        valueObject.setAuditRemark(auditRemark);
        valueObject.setAuditor(auditor);
        valueObject.setAuditName(auditName);
        valueObject.setAuditor(auditor);
        valueObject.setAuditTime(new Date());
        return valueObject;
    }

    public static AuditValueObject of(String auditRemark, String auditor, String auditName, Date auditTime) {
        AuditValueObject valueObject = new AuditValueObject();
        valueObject.setAuditRemark(auditRemark);
        valueObject.setAuditor(auditor);
        valueObject.setAuditName(auditName);
        valueObject.setAuditor(auditor);
        valueObject.setAuditTime(auditTime);
        return valueObject;
    }
}
