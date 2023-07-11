package com.yj2025.sample.jdbc.repository;

import com.yj2025.sample.jdbc.entity.TestUser;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestUserRepository extends ListCrudRepository<TestUser, Long>, PagingAndSortingRepository<TestUser, Long> {

    @Query("select * from test_user_#{#entCode} where code like CONCAT('%', :code,'%') ")
    List<TestUser> findList(@Param("entCode") String entCode, @Param("code") String code);

}
