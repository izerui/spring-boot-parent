package com.yj2025.sms;

import com.yj2025.sms.event.SmsSpringEvent;
import com.yj2025.sms.providers.SmsExecuteContext;
import com.yj2025.sms.providers.SmsExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
class SmsSenderImpl implements SmsSender {

    private static final String SMS_CAPTCHA_EXPIRE_KEY = "SMS:%s:%s";

    @Autowired
    private ObjectProvider<SmsExecutor> executors;
    private SmsExecutor _currentExecutor;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private SmsProperties properties;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private SmsExecutor getExecutor() {
        if (this._currentExecutor == null) {
            if (executors.stream().findAny().isEmpty()) {
                throw new SmsException("未配置有效的短信发送网关!");
            } else if (executors.stream().count() > 1L) {
                log.warn("发现多个在使用的短信网关，自动使用第一个发现的网关，建议检查配置!");
            }
            this._currentExecutor = executors.stream().findFirst().get();
        }
        return this._currentExecutor;
    }

    @Override
    public void sendContent(String template, Map<String, String> varaibles, String... phones) {
        try {
            if (varaibles == null) {
                varaibles = new HashMap<>();
            }
            SmsExecuteContext context = this.getExecutor().sendSMS(StringUtils.join(phones, ","), properties.getSignName(), template, varaibles);
            if (!context.isSuccess()) {
                throw new RuntimeException("无法发送短信：" + context.getErrMsg() + "[错误代码：" + context.getErrCode() + "]");
            }
            publisher.publishEvent(new SmsSpringEvent(this, context));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sendContent 短信发送失败，可能原因是配置错误，，，，，，，，，，，，，，本地环境，短信无法发送出去。。。。。。。请到数据库查看发送结果。。。。。");
        }
    }


    @Override
    public void sendCaptcha(String template, Function<String, Map<String, String>> varaiblesFun, String bizCode, long timeoutSeconds, String phone) {
        try {
            if (!isPhoneNumber(phone)) {
                throw new SmsException("手机号码不符合规则");
            }
            if (StringUtils.isBlank(bizCode)) {
                throw new SmsException("业务编号不能为空");
            }
            Assert.notNull(varaiblesFun, "变量集窗口不能为空");
            String expirePhoneKey = String.format(SMS_CAPTCHA_EXPIRE_KEY, bizCode, phone);
            BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(expirePhoneKey);
            Long expire = ops.getExpire();
            if (expire != null && expire > 0) {
                throw new SmsException("上次发送的验证码还有效,请不要重复发送");
            }
            String captcha = RandomStringUtils.randomNumeric(6);
            Map<String, String> varaibles = varaiblesFun.apply(captcha);
            SmsExecuteContext context = this.getExecutor().sendSMS(phone, properties.getSignName(), template, varaibles);
            if (!context.isSuccess()) {
                throw new RuntimeException("无法发送短信：" + context.getErrMsg() + "[错误代码：" + context.getErrCode() + "]");
            }
            ops.set(captcha, timeoutSeconds, TimeUnit.SECONDS);
            publisher.publishEvent(new SmsSpringEvent(this, context));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sendCaptcha 短信发送失败，可能原因是配置错误，，，，，，，，，，，，，，本地环境，短信无法发送出去。。。。。。。请到数据库查看发送结果。。。。。。");
        }
    }

    @Override
    public boolean isValidCaptcha(String bizCode, String captcha, String phone) {
        String expirePhoneKey = String.format(SMS_CAPTCHA_EXPIRE_KEY, bizCode, phone);
        String redisCaptcha = redisTemplate.boundValueOps(expirePhoneKey).get();
        if (StringUtils.isNotEmpty(redisCaptcha) && StringUtils.equals(captcha, redisCaptcha)) {
            return true;
        }
        return false;
    }

    /**
     * 验证手机号码是否正确，正确返回true，否则返回false
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) {
            return false;
        }

        //String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        /**
         * 2020年5月新增165、172、174、191、195 等号段的验证
         */

        // String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1、5、8、9]))\\d{8}$";
        /**
         * 2024年3月13日 新增，167，192,193,194,196,197
         */
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6,7])|(17[0-8])|(18[0-9])|(19[1-9]))\\d{8}$";
        if (phoneNumber.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phoneNumber);
            return m.matches();
        }
    }

}
