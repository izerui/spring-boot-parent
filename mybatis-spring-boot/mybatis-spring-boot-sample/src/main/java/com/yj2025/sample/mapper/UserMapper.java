package com.yj2025.sample.mapper;

import com.yj2025.mybatis.BasePageMapper;
import com.yj2025.sample.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface UserMapper extends BasePageMapper<User> {

    @Select("select * from test_user where ent_code = #{entCode} order by email desc")
    Page<User> findByOrigin(@Param("entCode") String entCode,
                            @Param("page") PageRequest page);
}
