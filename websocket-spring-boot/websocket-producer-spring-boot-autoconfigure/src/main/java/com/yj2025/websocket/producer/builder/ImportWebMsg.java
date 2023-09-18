package com.yj2025.websocket.producer.builder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yj2025.websocket.WebMsg;
import com.yj2025.websocket.producer.WebSocketContext;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 封装成业务主键，平台移除
 */
@Deprecated(since = "3.1", forRemoval = true)
public class ImportWebMsg {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * 企业账套编号
     */
    protected String entCode;
    /**
     * 发送的用户编码
     */
    protected String userCode;
    /**
     * 业务类型
     */
    protected String type;
    /**
     * 导入的状态
     */
    protected WebMsgStatusEnum status;

    /**
     * 请求唯一标识，用于第一次导入还未结束，产生第二次导入。导致错误消息显示到第二次上。
     */
    protected String requestID;

    protected ImportWebMsg(WebMsgStatusEnum status) {
        this.status = status;
    }

    public static PendingWebMsg pending() {
        return new PendingWebMsg();
    }

    public static SuccessWebMsg success() {
        return new SuccessWebMsg();
    }

    public static ErrorWebMsg error() {
        return new ErrorWebMsg();
    }

    public <T extends ImportWebMsg> T entCode(String entCode) {
        this.entCode = entCode;
        return (T) this;
    }

    public <T extends ImportWebMsg> T userCode(String userCode) {
        this.userCode = userCode;
        return (T) this;
    }

    public <T extends ImportWebMsg> T type(String type) {
        this.type = type;
        return (T) this;
    }

    public <T extends ImportWebMsg> T requestID(String requestID) {
        this.requestID = requestID;
        return (T) this;
    }

    protected WebMsg build() {
        WebMsg webMsg = new WebMsg(entCode, userCode, type);
        webMsg.set("status", status.name());
        if (StringUtils.hasText(requestID)) {
            webMsg.set("requestID", requestID);
        }
        return webMsg;
    }

    public void send(WebSocketContext context) {
        context.sendMessage(build());
    }

    public static class SuccessWebMsg extends ImportWebMsg {

        protected SuccessWebMsg() {
            super(WebMsgStatusEnum.SUCCESS);
        }

        @Override
        public SuccessWebMsg entCode(String entCode) {
            return super.entCode(entCode);
        }

        @Override
        public SuccessWebMsg userCode(String userCode) {
            return super.userCode(userCode);
        }

        @Override
        public SuccessWebMsg type(String type) {
            return super.type(type);
        }

        @Override
        public <T extends ImportWebMsg> T requestID(String requestID) {
            return super.requestID(requestID);
        }
    }

    public static class PendingWebMsg extends ImportWebMsg {
        /**
         * 总共条目数
         */
        private String totalRowNum;
        /**
         * 当前处理行数
         */
        private String currentRowNum;

        protected PendingWebMsg() {
            super(WebMsgStatusEnum.PENDING);
        }

        @Override
        public PendingWebMsg entCode(String entCode) {
            return super.entCode(entCode);
        }

        @Override
        public PendingWebMsg userCode(String userCode) {
            return super.userCode(userCode);
        }

        @Override
        public PendingWebMsg type(String type) {
            return super.type(type);
        }

        @Override
        public <T extends ImportWebMsg> T requestID(String requestID) {
            return super.requestID(requestID);
        }

        public PendingWebMsg totalRowNum(String totalRowNum) {
            this.totalRowNum = totalRowNum;
            return this;
        }

        public PendingWebMsg currentRowNum(String currentRowNum) {
            this.currentRowNum = currentRowNum;
            return this;
        }

        @Override
        protected WebMsg build() {
            WebMsg webMsg = super.build();
            webMsg.set("totalRowNum", totalRowNum);
            webMsg.set("currentRowNum", currentRowNum);
            return webMsg;
        }

    }

    public static class ErrorWebMsg extends ImportWebMsg {
        /**
         * 出错title提示信息
         */
        private String errorTitle;
        /**
         * 要显示的出错行列表及信息
         */
        private List<RowError> errorList;

        protected ErrorWebMsg() {
            super(WebMsgStatusEnum.ERROR);
        }

        @Override
        public ErrorWebMsg entCode(String entCode) {
            return super.entCode(entCode);
        }

        @Override
        public ErrorWebMsg userCode(String userCode) {
            return super.userCode(userCode);
        }

        @Override
        public ErrorWebMsg type(String type) {
            return super.type(type);
        }

        @Override
        public <T extends ImportWebMsg> T requestID(String requestID) {
            return super.requestID(requestID);
        }

        public ErrorWebMsg errorTitle(String errorTitle) {
            this.errorTitle = errorTitle;
            return this;
        }

        public ErrorWebMsg errorList(List<RowError> errorList) {
            this.errorList = errorList;
            return this;
        }

        @SneakyThrows
        @Override
        protected WebMsg build() {
            WebMsg webMsg = super.build();
            webMsg.set("errorTitle", errorTitle);
            webMsg.set("errorList", OBJECT_MAPPER.writeValueAsString(errorList));
            return webMsg;
        }
    }

    public static class RowError {
        @Getter
        private String rowNum;
        @Getter
        private String rowError;

        public RowError(String rowNum, String rowError) {
            this.rowNum = rowNum;
            this.rowError = rowError;
        }
    }
}
