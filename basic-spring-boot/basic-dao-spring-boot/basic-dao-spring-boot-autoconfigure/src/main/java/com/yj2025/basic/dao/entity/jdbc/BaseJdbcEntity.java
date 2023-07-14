package com.yj2025.basic.dao.entity.jdbc;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;

import java.util.Date;

@Data
public abstract class BaseJdbcEntity {

    /**
     * 主键
     */
    @Id
    protected long id;

    /**
     * 乐观锁
     */
    @Version
    protected long version;

    /**
     * 创建时间
     */
    @Column("create_time")
    @CreatedDate
    protected Date createTime = new Date();

    /**
     * 更新时间
     */
    @Column("update_time")
    @LastModifiedDate
    protected Date updateTime;

    /**
     * 删除状态
     */
    @Column("deleted")
    protected boolean deleted = false;

    /**
     * 删除时间
     */
    @Column("delete_time")
    protected Date deleteTime;
}
