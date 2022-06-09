package com.yj2025.mongo.repository;


import com.yj2025.jpa.PlatformJpaRepository;
import com.yj2025.mongo.entity.User;

public interface UserRepository extends PlatformJpaRepository<User, Long> {
}
