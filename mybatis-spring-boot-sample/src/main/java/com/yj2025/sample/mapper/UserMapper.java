package com.yj2025.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yj2025.mybatis.BasePageMapper;
import com.yj2025.sample.entity.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserMapper extends BasePageMapper<User> {

}
