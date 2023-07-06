package com.yj2025.jpa.repository;

import com.yj2025.jpa.PlatformJpaRepository;
import com.yj2025.jpa.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends PlatformJpaRepository<User, Long> {

    @Query("from #{#entityName} where code = :#{#code}")
    List<User> findList( @Param("code") String code);
}
