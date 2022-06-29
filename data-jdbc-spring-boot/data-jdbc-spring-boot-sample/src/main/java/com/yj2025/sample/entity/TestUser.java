package com.yj2025.sample.entity;

import lombok.Data;
import org.springframework.data.annotation.Version;

import java.util.Date;

//用户
@Data
public class TestUser {
    private Long id;
    @Version
    private int version;
    private Date createTime = new Date();
    private String code;
    private String name;
    private String email;
    private Integer age;
}
