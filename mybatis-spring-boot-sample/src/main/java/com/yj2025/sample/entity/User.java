package com.yj2025.sample.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    @TableId
    private Long id;
    private String entCode;
    private String name;
    private Integer age;
    private String email;
}
