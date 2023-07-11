package com.yj2025.sample.service;

import com.yj2025.sample.entity.TestUser;
import com.yj2025.sample.repository.TestUserRepository;
import com.yj2025.sharding.tenant.TenantThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SampleService {

    @Autowired
    private TestUserRepository testUserRepository;

    @TenantThreadLocal("#{#entCode}")
    public List<TestUser> findList(String entCode) {
        return testUserRepository.findAll();
    }

    public List<TestUser> findList1(String entCode) {
        return testUserRepository.findList1(entCode);
    }

}
