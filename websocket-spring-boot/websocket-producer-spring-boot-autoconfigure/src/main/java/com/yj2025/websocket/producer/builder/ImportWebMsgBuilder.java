package com.yj2025.websocket.producer.builder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yj2025.websocket.WebMsg;
import lombok.SneakyThrows;

import java.util.List;

public class ImportWebMsgBuilder {

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
    protected ImportStatusEnum status;

    protected ImportWebMsgBuilder(ImportStatusEnum status) {
        this.status = status;
    }

    public static PendingBuilder pendingBuilder() {
        return new PendingBuilder();
    }

    public static SuccessBuilder successBuilder() {
        return new SuccessBuilder();
    }

    public static ErrorBuilder errorBuilder() {
        return new ErrorBuilder();
    }

    public <T extends ImportWebMsgBuilder> T entCode(String entCode) {
        this.entCode = entCode;
        return (T) this;
    }

    public <T extends ImportWebMsgBuilder> T userCode(String userCode) {
        this.userCode = userCode;
        return (T) this;
    }

    public <T extends ImportWebMsgBuilder> T type(String type) {
        this.type = type;
        return (T) this;
    }

    public WebMsg build() {
        WebMsg webMsg = new WebMsg(entCode, userCode, type);
        webMsg.set("status", status.name());
        return webMsg;
    }


    public static class SuccessBuilder extends ImportWebMsgBuilder {

        protected SuccessBuilder() {
            super(ImportStatusEnum.SUCCESS);
        }

        @Override
        public SuccessBuilder entCode(String entCode) {
            return super.entCode(entCode);
        }

        @Override
        public SuccessBuilder userCode(String userCode) {
            return super.userCode(userCode);
        }

        @Override
        public SuccessBuilder type(String type) {
            return super.type(type);
        }
    }

    public static class PendingBuilder extends ImportWebMsgBuilder {
        /**
         * 总共条目数
         */
        private String totalRowNum;
        /**
         * 当前处理行数
         */
        private String currentRowNum;

        protected PendingBuilder() {
            super(ImportStatusEnum.PENDING);
        }

        @Override
        public PendingBuilder entCode(String entCode) {
            return super.entCode(entCode);
        }

        @Override
        public PendingBuilder userCode(String userCode) {
            return super.userCode(userCode);
        }

        @Override
        public PendingBuilder type(String type) {
            return super.type(type);
        }

        public PendingBuilder totalRowNum(String totalRowNum) {
            this.totalRowNum = totalRowNum;
            return this;
        }

        public PendingBuilder currentRowNum(String currentRowNum) {
            this.currentRowNum = currentRowNum;
            return this;
        }

        @Override
        public WebMsg build() {
            WebMsg webMsg = super.build();
            webMsg.set("totalRowNum", totalRowNum);
            webMsg.set("currentRowNum", currentRowNum);
            return webMsg;
        }

    }

    public static class ErrorBuilder extends ImportWebMsgBuilder {
        /**
         * 出错title提示信息
         */
        private String errorTitle;
        /**
         * 要显示的出错行列表及信息
         */
        private List<RowError> errorList;

        protected ErrorBuilder() {
            super(ImportStatusEnum.ERROR);
        }

        @Override
        public ErrorBuilder entCode(String entCode) {
            return super.entCode(entCode);
        }

        @Override
        public ErrorBuilder userCode(String userCode) {
            return super.userCode(userCode);
        }

        @Override
        public ErrorBuilder type(String type) {
            return super.type(type);
        }

        public ErrorBuilder errorTitle(String errorTitle) {
            this.errorTitle = errorTitle;
            return this;
        }

        public ErrorBuilder errorList(List<RowError> errorList) {
            this.errorList = errorList;
            return this;
        }

        @SneakyThrows
        @Override
        public WebMsg build() {
            WebMsg webMsg = super.build();
            webMsg.set("errorTitle", errorTitle);
            webMsg.set("errorList", OBJECT_MAPPER.writeValueAsString(errorList));
            return webMsg;
        }
    }

    public static class RowError {
        private String rowNum;
        private String rowError;

        public RowError(String rowNum, String rowError) {
            this.rowNum = rowNum;
            this.rowError = rowError;
        }
    }


}
