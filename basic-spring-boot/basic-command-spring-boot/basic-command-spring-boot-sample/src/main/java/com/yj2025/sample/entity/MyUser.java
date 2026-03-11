package com.yj2025.sample.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("test_user")
public class MyUser {
    @TableId
    private Long id;
    private String code;
    @Version
    private int version;
    private String entCode;
    private String name;
    private Integer age;
    private String email;
}
