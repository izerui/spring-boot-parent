package com.yj2025.sample.repository;

import com.yj2025.sample.entity.TestUser;
import com.yj2025.tenant.Tenant;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestUserRepository extends ListCrudRepository<TestUser, Long>, PagingAndSortingRepository<TestUser, Long> {

    @Query("select * from #{@sharding.getTable('test_user')} where code like CONCAT('%', :code,'%') ")
    List<TestUser> findList(@Param("entCode") String entCode, @Param("code") String code);


    @Tenant("#{#entCode}")
    List<TestUser> findByCode(String entCode, String code);

    @Tenant(value = "#{#entCode}", year = "#{#year}")
    @Query("select * from #{@sharding.getTable('test_user')}")
    List<TestUser> findList1(String entCode, Integer year);
}
