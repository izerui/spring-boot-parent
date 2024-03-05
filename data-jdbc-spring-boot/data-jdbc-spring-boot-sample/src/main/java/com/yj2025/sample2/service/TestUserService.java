package com.yj2025.sample2.service;

import com.yj2025.jdbc.dialect.flag.QueryFlagAfterTable;
import com.yj2025.jdbc.dialect.flag.QueryFlagAfterTables;
import com.yj2025.sample2.entity.TestUser;
import com.yj2025.sample2.mapping.GroupMapping;
import com.yj2025.sample2.repository.TestUserRepository;
import com.yj2025.tenant.Tenant;
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

    @Tenant("#{#entCode}")
    public Iterable<TestUser> findAll(String entCode) {
        log.info("tx: {}", TransactionSynchronizationManager.isActualTransactionActive());
        return testUserRepository.findAll();
    }

    @Tenant("#{#entCode}")
    public Iterable<TestUser> findAll(String entCode, Example example) {
        return testUserRepository.findAll(example);
    }

    @Tenant("#{#entCode}")
    public Page<TestUser> findByPage(String entCode, Pageable pageable) {
        return testUserRepository.findAll(pageable);
    }

    @Tenant("#{#user.entCode}")
    public void insertUser(TestUser user) {
        testUserRepository.save(user);
    }

    public List<TestUser> findByCode(String entCode, String code) {
        List<TestUser> users = testUserRepository.findByCode(entCode, code);
        return users;
    }

    @Tenant("#{#entCode}")
//    @QueryFlagAfterTables(
//            {
//                    @QueryFlagAfterTable(value = "force index(idx_01)", isComment = false, tablePrefix = "test_user_ent001"),
//                    @QueryFlagAfterTable(value = "force index(idx_02)", isComment = false, tablePrefix = "test_user_ent002")
//            }
//    )
    @QueryFlagAfterTable(value = "force index(idx_01)", isComment = false)
    @QueryFlagAfterTable(value = "force index(idx_02)", isComment = false, tablePrefix = "test_user_ent002")
    public Page<TestUser> findByQuery(String entCode, Query query) {
        return testUserRepository.findAll(query, PageRequest.of(0, 200));
    }

    @Tenant("#{#entCode}")
    public Page<TestUser> findByQuery2(String entCode, Query query) {
        return jdbcAggregateTemplate.findAll(query, TestUser.class, PageRequest.of(0, 200));
    }

    @Tenant("#{#map['ent_code']}")
    public Iterable findByMap(Map map, Sort sort) {
        return testUserRepository.findAll(map, sort);
    }

    @Tenant("#{#map['ent_code']}")
    public Page<TestUser> findByMapPage(Map map, Pageable pageable) {
        return testUserRepository.findAll(map, pageable);
    }

    @Tenant("#{#entCode}")
    public void batchInsert(String entCode, List<TestUser> users) {
        testUserRepository.batchInsert(users, "id");
    }

    @Tenant("#{#entCode}")
    public List<GroupMapping> groupList(String entCode, Query query, List<String> columns, List<String> groups) {
        return testUserRepository.groupAll(columns, groups, GroupMapping.class, query);
    }

    @Tenant("#{#entCode}")
    public Page<GroupMapping> groupPage(String entCode, Query query, List<String> columns, List<String> groups, Pageable pageable) {
        return testUserRepository.groupAll(columns, groups, GroupMapping.class, query, pageable);
    }

    @Tenant("#{#entCode}")
    public List<Map> groupList2(String entCode, Query query, List<String> columns, List<String> groups) {
        return testUserRepository.groupAll(columns, groups, Map.class, query);
    }

    @Tenant("#{#entCode}")
    public Iterable<TestUser> findByRecordIds(String entCode, List<Long> ids) {
        return testUserRepository.findAllById(ids);
    }
}
