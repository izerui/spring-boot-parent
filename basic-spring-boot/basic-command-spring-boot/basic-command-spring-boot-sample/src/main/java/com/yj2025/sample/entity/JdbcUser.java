package com.yj2025.sample.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

//用户 注意这里的Table 跟 jpa的Table 类不一样
@Data
@Table("#{@sharding.getTable('test_user')}")
public class JdbcUser {
    @Id
    private Long id;
    @Version
    private int version;
    private String entCode;
    @CreatedDate
    private Date createTime = new Date();
    private String code;
    private String name;
    private String email;
    private Integer age;
}
