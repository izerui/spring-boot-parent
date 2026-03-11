package com.yj2025.commons.vo;


/**
 * Copyright (C), 2015-2016, 深圳云集智造系统技术有限公司
 *
 * @Title: APP响应基本结构
 * @Description:
 * @Author by Tine
 * @date 2016/10/12 17:43
 */

import lombok.Data;

/**
 * 响应基本结构
 *
 * @param <T>
 * @author liuyuhua
 */
@Data
public class RespVO<T> {
    /**
     * 请求处理是否成功
     */
    private boolean success;

    /**
     * 错误编码
     */
    private String errCode;

    /**
     * 错误消息
     */
    private String errMsg;

    /**
     * 响应内容实体
     */
    private T data;

    /**
     * 当前做的唯一串，如单号...
     */
    private String unique;

    private Exception exception;

    protected RespVO() {
    }

    protected RespVO(boolean success, String errCode, String errMsg, T data) {
        this.success = success;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }

    protected RespVO(boolean success, String errCode, String errMsg, T data, String unique) {
        this.success = success;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
        this.unique = unique;
    }

    protected RespVO(boolean success, String errCode, String errMsg, T data, Exception exception) {
        this.success = success;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
        this.exception = exception;
    }

    /**
     * 成功
     */
    public static <T> RespVO<T> success() {
        return new RespVO<>(true, null, null, null);
    }

    /**
     * 成功
     */
    public static RespVO success(String msg, String unique) {
        return new RespVO<>(true, null, null, msg, unique);
    }

    /**
     * 成功
     */
    public static <T> RespVO<T> success(T data) {
        return new RespVO<>(true, null, null, data);
    }

    /**
     * 失败
     */
    public static <T> RespVO<T> error(String errCode, String errMsg) {
        return new RespVO<>(false, errCode, errMsg, null);
    }
}
