package com.yj2025.sample.repository;

import com.yj2025.jdbc.PlatformJdbcRepository;
import com.yj2025.sample.entity.TestUser;
import com.yj2025.sharding.tenant.TenantThreadLocal;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestUserRepository extends PlatformJdbcRepository<TestUser, Long> {

    @Query("select * from test_user_#{#entCode} where code like CONCAT('%', :code,'%') ")
    List<TestUser> findList(@Param("entCode") String entCode, @Param("code") String code);


    @TenantThreadLocal("#{#entCode}")
    List<TestUser> findByCode(String entCode, String code);

}
