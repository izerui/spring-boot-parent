package com.yj2025.basic.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class SubmitVO {
    @Schema(description = "提交人CODE")
    private String submitter;
    @Schema(description = "提交人名称")
    private String submitName;
    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date submitTime;
}
