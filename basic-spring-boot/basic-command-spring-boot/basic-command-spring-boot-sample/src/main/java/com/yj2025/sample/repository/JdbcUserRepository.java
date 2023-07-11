package com.yj2025.sample.repository;

import com.yj2025.sample.entity.JdbcUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JdbcUserRepository extends ListCrudRepository<JdbcUser, Long>, PagingAndSortingRepository<JdbcUser, Long> {

    @Query("select * from test_user_#{#entCode} where code like CONCAT('%', :code,'%') ")
    List<JdbcUser> findList(@Param("entCode") String entCode, @Param("code") String code);

}
