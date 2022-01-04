package com.yj2025.sms.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 短信记录
 * Created by LiMing on 2017-06-22.
 */
@Data
public class SmsAuditRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 记录ID
     **/
    private String id;

    /**
     * 应用名称
     **/
    private String appName;

    /**
     * 是否发送成功
     **/
    private boolean success;

    /**
     * 发送时间
     **/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    /**
     * 耗时
     */
    private Long time;

    /**
     * 请求内容
     **/
    private Object request;

    /**
     * 响应内容
     **/
    private Object response;

    /**
     * 错误信息
     **/
    private String error;

}
