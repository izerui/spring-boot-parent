package com.yj2025.sample.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

//用户
@Data
@Entity
@jakarta.persistence.Table(name = "test_user")
public class JpaUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private int version;
    private Date createTime = new Date();
    @Column(unique = true, nullable = false, updatable = false, length = 64)
    private String code;
    private String name;
    private String email;
    private Integer age;
    private String entCode;
}
