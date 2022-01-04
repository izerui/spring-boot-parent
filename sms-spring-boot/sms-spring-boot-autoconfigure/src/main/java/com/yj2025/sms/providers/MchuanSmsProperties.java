package com.yj2025.sms.providers;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * "名传无线"短信服务配置
 * Created by LiMing on 2017-06-22.
 */
@ConfigurationProperties(prefix = "mchuan.sms")
public class MchuanSmsProperties {
    /** HTTP请求地址 **/
    private String httpUrl = "http://112.74.139.4:8002/sms3_api/jsonapi/jsonrpc2.jsp";
    /** 账号 **/
    private String userid = "200099";
    /** 密码 **/
    private String password = "904f6ca73e8f5046a0698eb348f4fbfe2";
    /** 连接超时时间(毫秒) **/
    private int connectTimeout = 15_000;
    /** 读超时时间(毫秒) **/
    private int readTimeout = 30_000;
    /** 写超时时间(毫秒) **/
    private int writeTimeout = 15_000;

    public String getHttpUrl() {
        return httpUrl;
    }

    public MchuanSmsProperties setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
        return this;
    }

    public String getUserid() {
        return userid;
    }

    public MchuanSmsProperties setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public MchuanSmsProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public MchuanSmsProperties setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public MchuanSmsProperties setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public MchuanSmsProperties setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }
}
