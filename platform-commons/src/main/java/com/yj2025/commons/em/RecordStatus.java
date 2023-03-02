package com.yj2025.commons.em;

/**
 * Copyright (C), 2014-2015, 深圳云集智造系统技术有限公司
 *
 * @Title:
 * @Description :
 * @Author by yandw
 * @date on 2016/8/15
 */
public enum RecordStatus {
    /**
     * 删除
     */
    DELETE(-1),
    /**
     * 禁用
     */
    DISABLE(0),
    /**
     * 启用
     */
    ENABLE(1);

    private int status;

    private RecordStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static Boolean isDelete(int status) {
        return DELETE.getStatus() == status;
    }

    public static Boolean isEnable(int status) {
        return ENABLE.getStatus() == status;
    }

    public static Boolean isDisable(int status) {
        return DISABLE.getStatus() == status;
    }

    public static Boolean isDeleteStr(String status) {
        return DELETE.name().equals(status);
    }

    public static Boolean isEnableStr(String status) {
        return ENABLE.name().equals(status);
    }

    public static Boolean isDisableStr(String status) {
        return DISABLE.name().equals(status);
    }
}
