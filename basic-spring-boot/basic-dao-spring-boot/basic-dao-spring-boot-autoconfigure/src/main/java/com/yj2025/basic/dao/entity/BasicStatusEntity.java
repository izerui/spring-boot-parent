package com.yj2025.basic.dao.entity;

import com.yj2025.basic.dao.support.StatusEnum;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class BasicStatusEntity extends BasicEntity {

    /**
     * 记录状态 {@link StatusEnum}
     */
    @Column(name = "record_status", columnDefinition = "tinyint(4) COMMENT '记录状态 0:草稿，1:激活、启用，2:失效、停用，3:删除'", nullable = false)
    protected StatusEnum recordStatus;


    /**
     * 创建人
     */
    @Column(name = "creator", columnDefinition = "VARCHAR(64) COMMENT '创建人'", nullable = false)
    protected String creator;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'", nullable = false)
    protected Date createTime = new Date();

    /**
     * 更新人
     */
    @Column(name = "updater", columnDefinition = "VARCHAR(64) COMMENT '更新人'")
    protected String updater;

    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    protected Date updateTime;

    /**
     * 删除人
     */
    @Column(name = "deletor", columnDefinition = "VARCHAR(64) COMMENT '删除人'")
    protected String deletor;

    /**
     * 删除时间
     */
    @Column(name = "delete_time", columnDefinition = "DATETIME COMMENT '删除时间'")
    protected Date deleteTime;


    /**
     * 启用人
     */
    @Column(name = "activator", columnDefinition = "VARCHAR(64) COMMENT '启用人'")
    protected String activator;

    /**
     * 启用时间
     */
    @Column(name = "active_time", columnDefinition = "DATETIME COMMENT '启用时间'")
    protected Date activeTime;
}
