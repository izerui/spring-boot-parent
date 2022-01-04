package com.yj2025.sms.providers;

import lombok.Data;

import java.util.Date;

@Data
public class SmsExecuteContext {
    private boolean success;
    private String errCode;
    private String errMsg;
    private Object nativeRequest;
    private Object nativeResponse;
    private Date requestTime;
    private Date responseTime;
    private Long time;
    private String phones;
    private String content;

    public Long getTime() {
        if (requestTime != null && responseTime != null) {
            return responseTime.getTime() - requestTime.getTime();
        }
        return null;
    }

}
