package com.yj2025.basic.dao.value.object;

import lombok.Data;

import java.util.Date;

@Data
public class SubmitValueObject {
    /**
     * 提交人CODE
     */
    private String submitter;
    /**
     * 提交人名称
     */
    private String submitName;
    /**
     * 提交时间
     */
    protected Date submitTime;

    public static SubmitValueObject of(String submitter, String submitName, Date submitTime) {
        SubmitValueObject valueObject = new SubmitValueObject();
        valueObject.setSubmitter(submitter);
        valueObject.setSubmitName(submitName);
        valueObject.setSubmitTime(submitTime);
        return valueObject;
    }


    public static SubmitValueObject of(String submitter, String submitName) {
        SubmitValueObject valueObject = new SubmitValueObject();
        valueObject.setSubmitter(submitter);
        valueObject.setSubmitName(submitName);
        valueObject.setSubmitTime(new Date());
        return valueObject;
    }
}
