package com.yj2025.sample2.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.YearMonth;
import java.util.Date;

//用户
@Data
@Table("#{@sharding.getTable('test_user')}")
public class TestUser {
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
    private Boolean flag;
    private Boolean flagString;

    /**
     * 会计期间
     * <pre>测试年月是否可以自动转换</pre>
     */
    private YearMonth accountingPeriod;

    /**
     * 单据状态
     * <pre>测试枚举可能为空的情况</pre>
     */
    private DocStatus docStatus;


}
