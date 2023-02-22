package com.yj2025.basic.dao.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class BasicEntity extends BaseEntity{

    /**
     * 业务主键
     */
    @Column(
            name = "record_id",
            columnDefinition = "VARCHAR(64) COMMENT '业务主键'",
            unique = true,
            nullable = false)
    protected String recordId = UUID.randomUUID().toString();

    /**
     * 账套编号
     */
    @Column(name = "ent_code", columnDefinition = "VARCHAR(64) COMMENT '企业编码'", nullable = false)
    protected String entCode;

    /**
     * 创建人
     */
    @Column(name = "creator", columnDefinition = "VARCHAR(64) COMMENT '创建人'")
    protected String creator;

    /**
     * 更新人
     */
    @Column(name = "updater", columnDefinition = "VARCHAR(64) COMMENT '更新人'")
    protected String updater;

    /**
     * 删除人
     */
    @Column(name = "deleter", columnDefinition = "VARCHAR(64) COMMENT '删除人'")
    protected String deleter;

    /**
     * 修改更新人、更新时间
     * @param updater
     */
    public void updateBy(String updater) {
        this.updater = updater;
        this.updateTime = new Date();
    }

    /**
     * 修改删除人、删除时间
     * @param deleter
     */
    public void deleteBy(String deleter) {
        this.deleted = true;
        this.deleter = deleter;
        this.deleteTime = new Date();
    }

    public BasicEntity() {
    }

    public BasicEntity(String entCode, String creator) {
        this.entCode = entCode;
        this.creator = creator;
    }
}
