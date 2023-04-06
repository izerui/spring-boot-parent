package com.yj2025.basic.web.vo;

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
    protected Date submitTime;
}
