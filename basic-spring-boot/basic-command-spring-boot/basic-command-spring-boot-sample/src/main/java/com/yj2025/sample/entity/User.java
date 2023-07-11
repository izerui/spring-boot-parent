package com.yj2025.sample.entity;

import com.yj2025.performance.ClearEvent;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
    @NotBlank(message = "必须传入名称")
    private String name;
    private String email;
    private Integer age;
    private String entCode;

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
        map.put("ent_code", email);
        return map;
    }
}
