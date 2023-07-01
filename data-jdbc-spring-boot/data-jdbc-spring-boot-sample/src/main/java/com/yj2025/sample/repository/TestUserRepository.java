package com.yj2025.sample.repository;

import com.yj2025.sample.entity.TestUser;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface TestUserRepository extends ListCrudRepository<TestUser, Long>, PagingAndSortingRepository<TestUser, Long> {

}
