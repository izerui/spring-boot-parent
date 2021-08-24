package com.ecworking.audit;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.Map;

/**
 * Created by serv on 2016/12/8.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Record {
    /**
     * 应用名称
     */
    private String application;
    /**
     * 类型
     */
    private String type;
    /**
     * aop的类的信息
     */
    private String signature;
    /**
     * 名称
     */
    private String name;
    /**
     * 来源ip
     */
    private String ip;
    /**
     * 请求地址
     */
    private String url;
    /**
     * 平台账号
     */
    private String accountCode;
    /**
     * 手机号
     */
    private String accountName;
    /**
     * 用户编号
     */
    private String userCode;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 账套编号
     */
    private String entCode;
    /**
     * 账套名称
     */
    private String entName;
    /**
     * 成功状态
     */
    private boolean success;
    /**
     * 异常信息
     */
    private String exception;
    /**
     * 开始时间
     */
    private Date begin;
    /**
     * 结束时间
     */
    private Date end;
    /**
     * 耗时
     */
    private long time;
    /**
     * 来源ip
     */
    private String appId;
    /**
     * token
     */
    private String token;
    /**
     * 扩展信息
     */
    private Map<String, Object> info;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEntCode() {
        return entCode;
    }

    public void setEntCode(String entCode) {
        this.entCode = entCode;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
        this.time = end.getTime() - begin.getTime();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }
}
