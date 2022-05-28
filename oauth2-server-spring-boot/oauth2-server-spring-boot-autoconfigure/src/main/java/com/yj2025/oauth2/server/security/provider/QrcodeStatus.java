package com.yj2025.oauth2.server.security.provider;

import lombok.Data;

/**
 * Created by serv on 2016/12/20.
 */
@Data
public class QrcodeStatus {

    /**
     * 0:扫码,但是未确认登录,用来显示头像
     * 1:确认登录
     * -1:有效未使用的二维码
     * -2:无效的二维码
     */
    private int status;

    /**
     * 手机号
     */
    private String accountName;

    /**
     * 头像
     */
    private String accountPic;

    /**
     * 状态描述信息
     */
    private String description;

    /**
     * 扫码设置的账套编号
     */
    private String entCode;

    public QrcodeStatus() {
    }

    public QrcodeStatus(int status, String description) {
        this.status = status;
        this.description = description;
    }

    public QrcodeStatus(int status, String accountName, String accountPic, String description) {
        this.status = status;
        this.accountName = accountName;
        this.accountPic = accountPic;
        this.description = description;
    }

    public QrcodeStatus(int status, String accountName, String accountPic, String description, String entCode) {
        this.status = status;
        this.accountName = accountName;
        this.accountPic = accountPic;
        this.description = description;
        this.entCode = entCode;
    }
}