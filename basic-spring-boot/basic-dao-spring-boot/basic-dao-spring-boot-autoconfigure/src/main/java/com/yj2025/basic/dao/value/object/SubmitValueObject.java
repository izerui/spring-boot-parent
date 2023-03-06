package com.yj2025.basic.dao.value.object;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

@Data
@Embeddable
public class SubmitValueObject {
    @Column(name = "submitter", columnDefinition = "VARCHAR(64) COMMENT '提交人CODE'")
    private String submitter;
    @Column(name = "submit_name", columnDefinition = "VARCHAR(64) COMMENT '提交人名称'")
    private String submitName;
    @Column(name = "submit_time", columnDefinition = "DATETIME(3) COMMENT '提交时间'")
    protected Date submitTime;
}
