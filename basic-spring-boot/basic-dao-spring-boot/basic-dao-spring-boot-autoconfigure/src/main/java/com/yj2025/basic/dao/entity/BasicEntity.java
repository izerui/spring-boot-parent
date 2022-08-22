package com.yj2025.basic.dao.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class BasicEntity {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 乐观锁
     */
    @Version
    protected long version;

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
    @Column(name = "creator", columnDefinition = "VARCHAR(64) COMMENT '创建人'", nullable = false)
    protected String creator;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "DATETIME(3) COMMENT '创建时间'", nullable = false)
    protected Date createTime = new Date();

    /**
     * 更新人
     */
    @Column(name = "updater", columnDefinition = "VARCHAR(64) COMMENT '更新人'")
    protected String updater;

    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = "DATETIME(3) COMMENT '更新时间'")
    protected Date updateTime;

    /**
     * 删除状态
     */
    @Column(name = "deleted", columnDefinition = "bit(1) COMMENT '逻辑删除 0未删除 1删除'", nullable = false)
    protected boolean deleted = false;

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
     * @param deletor
     */
    public void deleteBy(String deletor) {
        this.deleted = true;
        this.updateTime = new Date();
    }

}
