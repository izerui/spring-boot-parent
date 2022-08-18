package com.yj2025.basic.dao.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class BasicEnabledEntity extends BasicEntity{

    @Column(name = "enabled", columnDefinition = "bit(1) COMMENT '是否启用 0禁用 1启用'", nullable = false)
    protected boolean enabled = true;
}
