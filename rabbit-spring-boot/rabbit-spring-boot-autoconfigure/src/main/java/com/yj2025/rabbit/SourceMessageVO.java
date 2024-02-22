package com.yj2025.rabbit;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class SourceMessageVO {
    private static final long serialVersionUID = 6202581238925445955L;
    /**
     * 消息发送时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private final Date createTime = new Date();
    /**
     * 企业账号编码
     */
    private String entCode;
    /**
     * 发送人CODE
     */
    private String userCode;
    /**
     * 发送人姓名
     */
    private String userName;
    /**
     * 单个业务主键
     */
    private String businessRecordId;
    /**
     * 多个业务主键
     */
    private List<String> businessRecordIds;

    public SourceMessageVO() {
    }

    private SourceMessageVO(String entCode, String userCode) {
        this.entCode = entCode;
        this.userCode = userCode;
    }

    private SourceMessageVO(String entCode, String userCode, String userName) {
        this.entCode = entCode;
        this.userCode = userCode;
        this.userName = userName;
    }

    public static SourceMessageVO of(String entCode, String userCode, String businessRecordId) {
        var result = new SourceMessageVO(entCode, userCode);
        result.businessRecordId = businessRecordId;
        return result;
    }

    public static SourceMessageVO of(String entCode, String userCode, String userName, String businessRecordId) {
        var result = new SourceMessageVO(entCode, userCode, userName);
        result.businessRecordId = businessRecordId;
        return result;
    }

    public static SourceMessageVO of(String entCode, String userCode, List<String> businessRecordIds) {
        var result = new SourceMessageVO(entCode, userCode);
        result.businessRecordIds = businessRecordIds;
        return result;
    }

    public static SourceMessageVO of(String entCode, String userCode, String userName, List<String> businessRecordIds) {
        var result = new SourceMessageVO(entCode, userCode, userName);
        result.businessRecordIds = businessRecordIds;
        return result;
    }

    public String getEntCode() {
        return entCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getUserName() {
        return userName;
    }

    public String getBusinessRecordId() {
        return businessRecordId;
    }

    public List<String> getBusinessRecordIds() {
        return businessRecordIds;
    }

    public Date getCreateTime() {
        return createTime;
    }
}
