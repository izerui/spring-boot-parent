package com.yj2025.oauth2.security.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Copyright (C), 2015-2016, 深圳云集智造系统技术有限公司
 *
 * @Title: APP响应基本结构
 * @Description:
 * @Author by Tine
 * @date 2016/10/12 17:43
 */
public class RespVo<T> {

    private final static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

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
    public static RespVo success(String msg, String unique) {
        return new RespVo<>(true, null, null, msg);
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

    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
