package com.yj2025.sample.repository;

import com.yj2025.jpa.PlatformJpaRepository;
import com.yj2025.sample.entity.User;

public interface UserRepository extends PlatformJpaRepository<User, Long> {
}
