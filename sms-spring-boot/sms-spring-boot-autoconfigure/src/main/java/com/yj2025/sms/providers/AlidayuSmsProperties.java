package com.yj2025.sms.providers;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * "阿里大于"短信服务配置
 * Created by LiMing on 2017-06-22.
 */
@ConfigurationProperties(prefix = "alidayu.sms")
public class AlidayuSmsProperties {
    /** HTTP请求地址 **/
    private String httpUrl = "http://gw.api.taobao.com/router/rest";
    /** app key **/
    private String appKey = "23295479";
    /** app secret **/
    private String appSecret = "06324a167c6410116ed766c7303be9ac";
    /** 返回格式(json或xml) **/
    private final String returnFormat = "json";
    /** 短信签名的摘要算法(hmac 或 md5) **/
    private final String signMethod = "hmac";
    /** 连接超时时间(毫秒) **/
    private int connectTimeout = 15000;
    /** 响应超时时间(毫秒) **/
    private int readTimeout = 30000;
    /** sms type(只能是 normal) **/
    private final String smsType = "normal";

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getReturnFormat() {
        return returnFormat;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getSmsType() {
        return smsType;
    }
}
