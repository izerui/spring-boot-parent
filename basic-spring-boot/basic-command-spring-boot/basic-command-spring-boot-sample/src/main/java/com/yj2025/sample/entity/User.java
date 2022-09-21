package com.yj2025.sample.entity;

import com.yj2025.performance.ClearEvent;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//用户
@Data
@Entity
@Table(name = "test_user")
public class User implements ClearEvent {
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

    @Override
    public void clear() {
        this.setId(null);
        this.setVersion(0);
        this.setCreateTime(new Date());
        this.setCode(null);
        this.setName(null);
        this.setEmail(null);
        this.setAge(null);
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("version", version);
        map.put("create_time", createTime);
        map.put("code", code);
        map.put("name", name);
        map.put("email", email);
        return map;
    }
}
