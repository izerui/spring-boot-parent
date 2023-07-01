package com.yj2025.sms.providers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yj2025.sms.SmsException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MchuanSmsExecutor implements SmsExecutor {

    private static final String CONTENT_TYPE = "application/json";

    @Autowired
    private MchuanSmsProperties properties;

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private OkHttpClient okHttpClient;

    @PostConstruct
    public void init() {
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(properties.getConnectTimeout(), timeUnit)
                .readTimeout(properties.getReadTimeout(), timeUnit)
                .writeTimeout(properties.getWriteTimeout(), timeUnit)
                .build();
    }

    @Override
    public SmsExecuteContext sendSMS(String phones, String signName, String template, Map<String, String> varaibles) throws SmsException {
        if (varaibles == null) {
            varaibles = new HashMap<>();
        }
        StringSubstitutor sub = new StringSubstitutor(varaibles);
        String content = sub.replace(template);
        SmsExecuteContext context = new SmsExecuteContext();
        this.send(phones, content, context);
        return context;
    }


    private void send(String phones, String content, SmsExecuteContext context) {
        if (StringUtils.isBlank(phones)) {
            throw new SmsException("手机号不能为空");
        }
        if (StringUtils.isBlank(content)) {
            throw new SmsException("短信内容不能为空");
        }

        try {
            log.info("[{}]: {}", phones, content);
            context.setRequestTime(new Date());
            Params params = new Params(properties.getUserid(), properties.getPassword(), phones, content);
            SmsSendRequest request = new SmsSendRequest();
            request.setParams(params);

            Request req = new Request.Builder()
                    .url(properties.getHttpUrl())
                    .post(RequestBody.create(MediaType.parse(CONTENT_TYPE), objectMapper.writeValueAsString(request)))
                    .build();

            Response resp = okHttpClient.newCall(req).execute();
            String body = resp.body().string();
            if (!resp.isSuccessful()) {
                throw new SmsException(body);
            }
            ObjectMapper mapper = new ObjectMapper();
            SmsSendResponse response = mapper.readValue(body, SmsSendResponse.class);
            context.setNativeRequest(request);
            context.setSuccess(!response.hasError());
            context.setNativeResponse(response);
            context.setResponseTime(new Date());
            context.setPhones(phones);
            context.setContent(content);
            if (response.getError() != null) {
                context.setErrCode(String.valueOf(response.getError().getCode()));
                context.setErrMsg(response.getError().getMessage());
            }
        } catch (IOException ex) {
            throw new SmsException(ex.getMessage());
        }
    }

    /**
     * "名传无线"短信发送所需参数
     * Created by LiMing on 2017-06-20.
     */
    @Data
    private static class Params {

        /**
         * 账号
         **/
        private String userid;

        /**
         * 密码
         **/
        private String password;

        /**
         * 短信列表
         **/
        private final List<SmsItem> submit = new ArrayList<>();

        public Params() {

        }

        public Params(String userid, String password, String phone, String content) {
            this.userid = userid;
            this.password = password;
            SmsItem sms = new SmsItem(phone, content);
            this.submit.add(sms);
        }

    }

    /**
     * 短信条目
     * Created by LiMing on 2017-06-20.
     */
    @Data
    private static class SmsItem {
        /**
         * 手机号，多个则用英文逗号分开
         **/
        private String phone;

        /**
         * 短信内容
         **/
        private String content;

        public SmsItem() {

        }

        public SmsItem(String phone, String content) {
            this.phone = phone;
            this.content = content;
        }

    }

    /**
     * 短信发送请求体
     * Created by LiMing on 2017-06-20.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private static class SmsSendRequest {
        /**
         * 编号(可选)
         **/
        private Integer id;

        /**
         * 接口类型，发送短信为send(必填)
         **/
        private final String method = "send";

        /**
         * 相关参数
         **/
        private Params params;

    }

    /**
     * Created by LiMing on 2017-06-20.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private static class SmsSendResponse {
        private Integer id;

        private List<ResultItem> result;

        private Error error;

        public boolean hasError() {
            return this.error != null;
        }


        /**
         * 结果列表项
         */
        @Data
        private static class ResultItem {
            /**
             * 描述
             **/
            private String info;

            /**
             * 序列号
             **/
            private String msgid;

            /**
             * 手机号码
             **/
            private String phone;

            /**
             * 状态码
             **/
            @JsonProperty("return")
            private String _return;

            private Integer mcount;

        }

        /**
         * 错误信息
         */
        @Data
        private static class Error {
            /**
             * 错误码
             **/
            private int code;
            /**
             * 错误描述
             **/
            private String message;

        }

    }
}
