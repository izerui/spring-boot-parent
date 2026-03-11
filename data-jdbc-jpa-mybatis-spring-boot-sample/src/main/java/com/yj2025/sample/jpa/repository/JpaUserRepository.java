package com.yj2025.sample.jpa.repository;

import com.yj2025.jpa.PlatformJpaRepository;
import com.yj2025.sample.jpa.entity.JpaUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaUserRepository extends PlatformJpaRepository<JpaUser, Long> {

    @Query("from JpaUser where entCode = :entCode")
    List<JpaUser> findList(@Param("entCode") String entCode);
}
