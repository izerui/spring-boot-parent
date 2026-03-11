package com.yj2025.sms;

import java.util.Map;
import java.util.function.Function;

public interface SmsSender {

    /**
     * 发送短信内容
     *
     * @param template  模板编号或者模板内容
     * @param varaibles 替换变量集
     * @param phones    手机号
     */
    void sendContent(String template, Map<String, String> varaibles, String... phones);


    /**
     * 发送验证码
     *
     * @param template       模板编号或者模板内容
     * @param varaiblesFun   入参为验证码，返回一个变量集
     * @param bizCode        业务标识
     * @param timeoutSeconds 验证码超时时间(秒)
     * @param phone          单个手机号
     */
    void sendCaptcha(String template, Function<String, Map<String, String>> varaiblesFun, String bizCode, long timeoutSeconds, String phone);

    /**
     * 验证码有效性
     *
     * @param bizCode 业务标识
     * @param captcha 验证码
     * @param phone   手机号
     * @return
     */
    boolean isValidCaptcha(String bizCode, String captcha, String phone);
    /**
     * 验证码有效性 同事销毁
     *
     * @param bizCode 业务标识
     * @param captcha 验证码
     * @param phone   手机号
     * @return
     */
    boolean checkAndDestroyCaptcha(String bizCode, String captcha, String phone);
}
