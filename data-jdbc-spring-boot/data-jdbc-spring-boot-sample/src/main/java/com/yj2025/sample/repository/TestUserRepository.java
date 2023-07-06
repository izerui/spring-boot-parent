package com.yj2025.sample.repository;

import com.yj2025.jdbc.tenant.TenantThreadLocal;
import com.yj2025.sample.entity.TestUser;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestUserRepository extends ListCrudRepository<TestUser, Long>, PagingAndSortingRepository<TestUser, Long> {

    @Query("select * from test_user_#{#entCode} where code like CONCAT('%', :code,'%') ")
    List<TestUser> findList(@Param("entCode") String entCode, @Param("code") String code);


    @TenantThreadLocal("#{#entCode}")
    List<TestUser> findByCode(String entCode, String code);

}
