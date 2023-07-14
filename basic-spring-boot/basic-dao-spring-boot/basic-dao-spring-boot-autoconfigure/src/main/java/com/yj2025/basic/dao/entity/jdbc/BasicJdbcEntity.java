package com.yj2025.basic.dao.entity.jdbc;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

import java.util.Date;
import java.util.UUID;

@Data
public abstract class BasicJdbcEntity extends BaseJdbcEntity {

    /**
     * 业务主键
     */
    @Column("record_id")
    protected String recordId = UUID.randomUUID().toString();

    /**
     * 账套编号
     */
    @Column("ent_code")
    protected String entCode;

    /**
     * 创建人
     */
    @Column("creator")
    protected String creator;

    /**
     * 更新人
     */
    @Column("updater")
    protected String updater;

    /**
     * 删除人
     */
    @Column("deletor")
    protected String deletor;

    /**
     * 修改更新人、更新时间
     *
     * @param updater
     */
    public void updateBy(String updater) {
        this.updater = updater;
        this.updateTime = new Date();
    }

    /**
     * 修改删除人、删除时间
     *
     * @param deletor
     */
    public void deleteBy(String deletor) {
        this.deleted = true;
        this.deletor = deletor;
        this.deleteTime = new Date();
    }

    public BasicJdbcEntity() {
    }

    public BasicJdbcEntity(String entCode, String creator) {
        this.entCode = entCode;
        this.creator = creator;
    }
}
