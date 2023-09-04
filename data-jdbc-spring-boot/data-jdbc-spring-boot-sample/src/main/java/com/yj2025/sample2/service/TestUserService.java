package com.yj2025.sample2.service;

import com.yj2025.sample2.entity.TestUser;
import com.yj2025.sample2.mapping.GroupMapping;
import com.yj2025.sample2.repository.TestUserRepository;
import com.yj2025.sharding.tenant.ShardingTenant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class TestUserService {

    @Autowired
    private TestUserRepository testUserRepository;
    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @ShardingTenant("#{#entCode}")
    public Iterable<TestUser> findAll(String entCode) {
        log.info("tx: {}", TransactionSynchronizationManager.isActualTransactionActive());
        return testUserRepository.findAll();
    }

    @ShardingTenant("#{#entCode}")
    public Iterable<TestUser> findAll(String entCode, Example example) {
        return testUserRepository.findAll(example);
    }

    @ShardingTenant("#{#entCode}")
    public Page<TestUser> findByPage(String entCode, Pageable pageable) {
        return testUserRepository.findAll(pageable);
    }

    @ShardingTenant("#{#user.entCode}")
    public void insertUser(TestUser user) {
        testUserRepository.save(user);
    }

    public List<TestUser> findByCode(String entCode, String code) {
        List<TestUser> users = testUserRepository.findByCode(entCode, code);
        return users;
    }

    @ShardingTenant("#{#entCode}")
    public Page<TestUser> findByQuery(String entCode, Query query) {
        return testUserRepository.findAll(query, PageRequest.of(0, 200));
    }

    @ShardingTenant("#{#entCode}")
    public Page<TestUser> findByQuery2(String entCode, Query query) {
        return jdbcAggregateTemplate.findAll(query, TestUser.class, PageRequest.of(0, 200));
    }

    @ShardingTenant("#{#map['ent_code']}")
    public Iterable findByMap(Map map, Sort sort) {
        return testUserRepository.findAll(map, sort);
    }

    @ShardingTenant("#{#map['ent_code']}")
    public Page<TestUser> findByMapPage(Map map, Pageable pageable) {
        return testUserRepository.findAll(map, pageable);
    }

    @ShardingTenant("#{#entCode}")
    public void batchInsert(String entCode, List<TestUser> users) {
        testUserRepository.batchInsert(users, "id");
    }

    @ShardingTenant("#{#entCode}")
    public List<GroupMapping> groupList(String entCode, Query query, List<String> columns, List<String> groups) {
        return testUserRepository.groupAll(columns, groups, GroupMapping.class, query);
    }

    @ShardingTenant("#{#entCode}")
    public Page<GroupMapping> groupPage(String entCode, Query query, List<String> columns, List<String> groups, Pageable pageable) {
        return testUserRepository.groupAll(columns, groups, GroupMapping.class, query, pageable);
    }

    @ShardingTenant("#{#entCode}")
    public List<Map> groupList2(String entCode, Query query, List<String> columns, List<String> groups) {
        return testUserRepository.groupAll(columns, groups, Map.class, query);
    }

    @ShardingTenant("#{#entCode}")
    public Iterable<TestUser> findByRecordIds(String entCode, List<Long> ids) {
        return testUserRepository.findAllById(ids);
    }
}
