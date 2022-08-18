package com.yj2025.basic.dao.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class BasicEnabledEntity extends BasicEntity{

    /**
     * 启用状态
     */
    @Column(name = "enabled", columnDefinition = "bit(1) COMMENT '是否启用 0禁用 1启用'", nullable = false)
    protected boolean enabled = true;

    /**
     * 启用时间
     */
    @Column(name = "enable_time", columnDefinition = "datetime DEFAULT NULL COMMENT '启用时间'", nullable = false)
    private Date enableTime;
}
