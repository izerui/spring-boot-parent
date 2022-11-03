package com.yj2025.basic.dao.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    /**
     * 乐观锁
     */
    @Version
    protected long version;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "DATETIME(3) COMMENT '创建时间'", nullable = false)
    protected Date createTime = new Date();

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
     * 删除时间
     */
    @Column(name = "delete_time", columnDefinition = "DATETIME(3) COMMENT '删除时间'")
    protected Date deleteTime;
}
