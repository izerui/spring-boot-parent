package com.yj2025.sms.providers;

//import com.taobao.api.DefaultTaobaoClient;
//import com.taobao.api.TaobaoClient;
//import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
//import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import com.yj2025.sms.SmsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

//import java.util.Date;
import java.util.Map;

@Slf4j
public class AlidayuSmsExecutor implements SmsExecutor {

    @Autowired
    private AlidayuSmsProperties properties;

    @Override
    public SmsExecuteContext sendSMS(String phones, String signName, String template, Map<String, String> varaibles) throws SmsException {
        SmsExecuteContext context = new SmsExecuteContext();
//        TaobaoClient client = new DefaultTaobaoClient(
//                properties.getHttpUrl(),
//                properties.getAppKey(),
//                properties.getAppSecret(),
//                properties.getReturnFormat(),
//                properties.getConnectTimeout(),
//                properties.getReadTimeout(),
//                properties.getSignMethod());
//        String smsParams = buildSmsParams(varaibles);
//
//        AlibabaAliqinFcSmsNumSendRequest request = new AlibabaAliqinFcSmsNumSendRequest();
//        request.setSmsType(properties.getSmsType());
//        request.setSmsFreeSignName(signName);
//        request.setRecNum(phones);
//        request.setSmsTemplateCode(template);
//        if (smsParams != null && !smsParams.isEmpty()) {
//            request.setSmsParam(smsParams);
//        }
//
//        AlibabaAliqinFcSmsNumSendResponse response;
//        try {
//            context.setRequestTime(new Date());
//            response = client.execute(request);
//            context.setResponseTime(new Date());
//        } catch (Exception ex) {
//            throw new SmsException(ex.getMessage());
//        }
//        log.info("[{}]: {}", phones, template + ": " + String.valueOf(varaibles));
//        context.setNativeRequest(request);
//        context.setSuccess(response.isSuccess());
//        context.setErrCode(response.getErrorCode());
//        context.setErrMsg(response.getMsg());
//        context.setNativeResponse(response);
//        context.setPhones(phones);
//        context.setContent(template + ": " + String.valueOf(varaibles));
        return context;
    }

    /**
     * 将Map类型的短信参数转换为json字符串
     *
     * @param params
     * @return
     */
    private String buildSmsParams(Map<String, String> params) {
        String result = null;
        if (params != null && !params.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("{");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append("\"").append(entry.getKey()).append("\"")
                        .append(":\"").append(entry.getValue()).append("\",");
            }
            builder.deleteCharAt(builder.length() - 1).append("}");
            result = builder.toString();
        }
        return result;
    }


}
