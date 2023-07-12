package com.yj2025.sample.service;

import com.yj2025.sample.entity.TestUser;
import com.yj2025.sample.repository.TestUserRepository;
import com.yj2025.sharding.tenant.TenantThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Slf4j
@Service
@Transactional
public class TestUserService {

    @Autowired
    private TestUserRepository testUserRepository;
    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @TenantThreadLocal("#{#entCode}")
    public Iterable<TestUser> findAll(String entCode) {
        log.info("tx: {}", TransactionSynchronizationManager.isActualTransactionActive());
        return testUserRepository.findAll();
    }

    @TenantThreadLocal("#{#entCode}")
    public Page<TestUser> findByPage(String entCode, Pageable pageable) {
        return testUserRepository.findAll(pageable);
    }

    @TenantThreadLocal("#{#user.entCode}")
    public void insertUser(TestUser user) {
        testUserRepository.save(user);
    }

    public List<TestUser> findByCode(String entCode, String code) {
        List<TestUser> users = testUserRepository.findByCode(entCode, code);
        return users;
    }

    @TenantThreadLocal("#{#entCode}")
    public Page<TestUser> findByQuery(String entCode, Query query) {
        return testUserRepository.findAll(query, PageRequest.of(0, 200));
    }

    @TenantThreadLocal("#{#entCode}")
    public Page<TestUser> findByQuery2(String entCode, Query query) {
        return jdbcAggregateTemplate.findAll(query, TestUser.class, PageRequest.of(0, 200));
    }
}
