package com.yj2025.sample.mapper;

import com.yj2025.mybatis.BasePageMapper;
import com.yj2025.sample.entity.Simple;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SimpleMapper extends BasePageMapper<Simple> {

    @Select("select * from d_simple d_simple where type = #{type} order by word desc")
    Page<Simple> findByOrigin(@Param("page") PageRequest page, @Param("type") String type);
}
