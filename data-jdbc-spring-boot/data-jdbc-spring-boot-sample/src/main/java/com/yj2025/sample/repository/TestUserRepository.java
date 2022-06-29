package com.yj2025.sample.repository;

import com.yj2025.sample.entity.TestUser;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TestUserRepository extends PagingAndSortingRepository<TestUser, Long> {

}
