package com.yj2025.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.Version;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import java.util.Date;

//用户
@Data
@Entity
@Table(name = "test_user")
public class User {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @org.springframework.data.annotation.Id
    private String mid;

    @Version
    private int version;
    private Date createTime = new Date();
    private String code;
    private String name;
    private String email;
    private Integer age;
}
