package com.yj2025.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Map;

public interface BasePageMapper<T> extends BaseMapper<T> {

    Page<T> selectPage(PageRequest pageRequest, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    Page<Map<String, Object>> selectMapsPage(PageRequest page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
}
