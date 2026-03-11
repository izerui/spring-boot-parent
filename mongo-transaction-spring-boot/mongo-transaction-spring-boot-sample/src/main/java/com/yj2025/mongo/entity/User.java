package com.yj2025.mongo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Version;

import java.util.Date;

//用户
@Data
@Entity
@Table(name = "test_user")
public class User {

    @Id
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
