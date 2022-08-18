package com.yj2025.basic.dao.entity;

import lombok.Data;

import javax.persistence.*;
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

}
