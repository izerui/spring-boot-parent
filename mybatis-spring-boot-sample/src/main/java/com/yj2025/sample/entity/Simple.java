package com.yj2025.sample.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("d_simple")
@Data
public class Simple {

    @TableId
    private Long id;
    private String word;
    private String simple;
    private String type;
}
