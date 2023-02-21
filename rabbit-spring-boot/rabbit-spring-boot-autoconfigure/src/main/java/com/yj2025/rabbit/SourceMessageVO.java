package com.yj2025.rabbit;

import java.util.Date;
import java.util.List;

public class SourceMessageVO {
    private static final long serialVersionUID = 6202581238925445955L;
    /**
     * 消息发送时间
     */
    private final Date createTime = new Date();
    /**
     * 企业账号编码
     */
    private String entCode;
    /**
     * 发送人
     */
    private String userCode;
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

    public static SourceMessageVO of(String entCode, String userCode, String businessRecordId) {
        var result = new SourceMessageVO(entCode, userCode);
        result.businessRecordId = businessRecordId;
        return result;
    }

    public static SourceMessageVO of(String entCode, String userCode, List<String> businessRecordIds) {
        var result = new SourceMessageVO(entCode, userCode);
        result.businessRecordIds = businessRecordIds;
        return result;
    }

    public String getEntCode() {
        return entCode;
    }

    public String getUserCode() {
        return userCode;
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
