package com.yj2025.open.commons;


/**
 * Copyright (C), 2015-2016, 深圳云集智造系统技术有限公司
 *
 * @Title: APP响应基本结构
 * @Description:
 * @Author by Tine
 * @date 2016/10/12 17:43
 */

/**
 * 响应基本结构
 *
 * @param <T>
 * @author liuyuhua
 */
public class RespVo<T> {
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

    protected RespVo() {
    }

    protected RespVo(boolean success, String errCode, String errMsg, T data) {
        this.success = success;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }

    /**
     * 成功
     */
    public static RespVo success() {
        return new RespVo<>(true, null, null, null);
    }

    /**
     * 成功
     */
    public static <T> RespVo success(T data) {
        return new RespVo<>(true, null, null, data);
    }

    /**
     * 失败
     */
    public static RespVo error(String errCode, String errMsg) {
        return new RespVo<>(false, errCode, errMsg, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public T getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setData(T data) {
        this.data = data;
    }

}
