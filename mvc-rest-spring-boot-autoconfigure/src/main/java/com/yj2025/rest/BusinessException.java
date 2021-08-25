package com.yj2025.rest;

/**
 * Created by serv on 2016/10/18.
 */
public class BusinessException extends RuntimeException {

    private String errCode;

    /**
     * 业务异常
     * @param message 信息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 业务异常
     * @param message 信息
     * @param errCode 错误码
     */
    public BusinessException(String message, String errCode) {
        super(message);
        this.errCode = errCode;
    }

    /**
     * 业务异常
     * @param message 信息
     * @param cause 异常堆栈
     * @param errCode 错误码
     */
    public BusinessException(String message, Throwable cause, String errCode) {
        super(message, cause);
        this.errCode = errCode;
    }

    /**
     * 业务异常
     * @param cause 异常堆栈
     * @param errCode 错误码
     */
    public BusinessException(Throwable cause, String errCode) {
        super(cause);
        this.errCode = errCode;
    }

    public String getErrCode() {
        return errCode;
    }
}
