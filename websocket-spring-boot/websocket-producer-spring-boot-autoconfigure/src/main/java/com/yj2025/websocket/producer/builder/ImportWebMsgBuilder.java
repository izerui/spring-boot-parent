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
    private String entCode;
    /**
     * 发送的用户编码
     */
    private String userCode;
    /**
     * 业务类型
     */
    private String type;
    /**
     * 导入的状态
     */
    private ImportStatusEnum status;

    public static PendingBuilder pendingBuilder() {
        return new PendingBuilder().status(ImportStatusEnum.PENDING);
    }

    public static SuccessBuilder successBuilder() {
        return new SuccessBuilder().status(ImportStatusEnum.SUCCESS);
    }

    public static ErrorBuilder errorBuilder() {
        return new ErrorBuilder().status(ImportStatusEnum.ERROR);
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

    <T extends ImportWebMsgBuilder> T status(ImportStatusEnum status) {
        this.status = status;
        return (T) this;
    }

    public WebMsg build() {
        WebMsg webMsg = new WebMsg(entCode, userCode, type);
        webMsg.set("status", status.name());
        return webMsg;
    }


    public static class SuccessBuilder extends ImportWebMsgBuilder {

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
