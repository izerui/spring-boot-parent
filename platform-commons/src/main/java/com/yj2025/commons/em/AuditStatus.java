package com.yj2025.commons.em;

public enum AuditStatus {
    //草稿
    DRAFT(-1,"草稿"),
    //待审核
    AUDITING(0,"待审核"),
    //待确认
    CONFIRM(-2,"待确认"),
    //已审核
    AUDITED(1,"已通过"),
    //已驳回
    REJECT(2,"已驳回"),
    //已撤回
    REVOKED(3,"已撤回"),
    //已回退
    ROLLBACK(4,"已回退"),
    //会审中
    MEETING(5,"会审中");

    private int status;
    private String remark;

    AuditStatus(int status) {
        this.status = status;
    }
    AuditStatus(int status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public static AuditStatus getAuditStatus(String name) {
        for (AuditStatus auditStatus : AuditStatus.values()) {
            if (auditStatus.name().equalsIgnoreCase(name)) {
                return auditStatus;
            }
        }
        return null;
    }

    public static AuditStatus getAuditStatus(int status) {
        for (AuditStatus auditStatus : AuditStatus.values()) {
            if (auditStatus.getStatus()==status) {
                return auditStatus;
            }
        }
        return null;
    }

    public static boolean isDraft(int auditStatus) {
        return DRAFT.getStatus() == auditStatus;
    }
    public static boolean isConfirm(int auditStatus) {
        return CONFIRM.getStatus() == auditStatus;
    }

    public static boolean isAuditing(int auditStatus) {
        return AUDITING.getStatus() == auditStatus;
    }

    public static boolean isAudited(int auditStatus) {
        return AUDITED.getStatus() == auditStatus;
    }

    public static boolean isReject(int auditStatus) {
        return REJECT.getStatus() == auditStatus;
    }

    public static boolean isRevoked(int auditStatus) {
        return REVOKED.getStatus() == auditStatus;
    }

    public String getRemark() {
        return remark;
    }
}
