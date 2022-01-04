package com.yj2025.sms.providers;

import com.yj2025.sms.SmsException;

import java.util.Map;

public interface SmsExecutor {
    /**
     * 发送短信
     * @param phones 短信接收者, 每个接收者为11位手机号码, 多个接收者之间用英文逗号分隔
     * @param signName 短信签名 例如： 【我的经管】
     * @param template 短信模板编码或者内容
     * @param varaibles 短信模板或者内容替换变量
     * @throws SmsException
     */
    SmsExecuteContext sendSMS(String phones, String signName, String template, Map<String, String> varaibles) throws SmsException;
}
