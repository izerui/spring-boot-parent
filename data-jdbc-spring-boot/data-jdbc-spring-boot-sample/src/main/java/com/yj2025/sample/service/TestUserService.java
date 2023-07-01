package com.yj2025.sample.service;

import com.yj2025.sample.entity.TestUser;
import com.yj2025.sample.repository.TestUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@Transactional
public class TestUserService {

    @Autowired
    private TestUserRepository testUserRepository;

    public Iterable<TestUser> findAll() {
        log.info("tx: {}", TransactionSynchronizationManager.isActualTransactionActive());
        return testUserRepository.findAll();
    }

    public Page<TestUser> findByPage(Pageable pageable) {
        return testUserRepository.findAll(pageable);
    }
}
