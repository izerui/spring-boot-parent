package com.yj2025.sample.repository;

import com.yj2025.sample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
