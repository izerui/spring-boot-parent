package com.yj2025.sample2.repository;

import com.yj2025.jdbc.PlatformJdbcRepository;
import com.yj2025.jdbc.dialect.flag.QueryFlagAfterTable;
import com.yj2025.sample2.entity.TestUser;
import com.yj2025.sharding.tenant.TenantThreadLocal;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TestUserRepository extends PlatformJdbcRepository<TestUser, Long> {

    @Query("select * from test_user_#{#entCode} where code like CONCAT('%', :code,'%') ")
    List<TestUser> findList(@Param("entCode") String entCode, @Param("code") String code);


    @TenantThreadLocal("#{#entCode}")
    @QueryFlagAfterTable("query标注: #{#code}")
    List<TestUser> findByCode(String entCode, String code);

    @TenantThreadLocal("#{#map['ent_code']}")
    @Override
    @QueryFlagAfterTable("query标注: #{#code}")
    List<TestUser> findAll(Map<String, Object> map);

}
