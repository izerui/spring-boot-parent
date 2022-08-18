package com.yj2025.basic.dao.support;

public enum StatusEnum {
    DRAFT(0, "草稿"),
    ACTIVE(1, "激活、启用"),
    INACTIVE(2, "失效、禁用"),
    DELETED(3, "删除");

    private Integer status;
    private String label;

    StatusEnum(Integer status, String label) {
        this.status = status;
        this.label = label;
    }

    public Integer getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }
}
