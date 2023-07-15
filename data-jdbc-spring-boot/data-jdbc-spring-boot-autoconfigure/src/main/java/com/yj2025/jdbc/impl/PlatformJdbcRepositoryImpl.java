package com.yj2025.jdbc.impl;

import com.yj2025.basic.support.DbContext;
import com.yj2025.jdbc.PlatformJdbcRepository;
import com.yj2025.jdbc.support.CriteriaUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.sql.SqlIdentifier;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @author liuyuhua
 * @date 2023年07月12日
 */
public class PlatformJdbcRepositoryImpl<T, ID> extends SimpleJdbcRepository<T, ID> implements PlatformJdbcRepository<T, ID> {

    private final JdbcAggregateTemplate jdbcAggregateTemplate;
    private final PersistentEntity<T, ?> entity;

    public PlatformJdbcRepositoryImpl(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity, JdbcConverter converter) {
        super(entityOperations, entity, converter);
        this.jdbcAggregateTemplate = (JdbcAggregateTemplate) entityOperations;
        this.entity = entity;
    }


    @Override
    public T insert(T instance) {
        return jdbcAggregateTemplate.insert(instance);
    }

    @Override
    public Iterable<T> insertAll(Iterable<T> instances) {
        return jdbcAggregateTemplate.insertAll(instances);
    }

    @Override
    public void batchInsert(Collection<T> instances, String... generatedKeys) {
        SqlIdentifier identifier = ((RelationalPersistentEntity<T>) entity).getTableName();
        DbContext.batchInsert(identifier.getReference(), instances, generatedKeys);
    }

    @Override
    public <U> void batchUpdate(String namedSQL, Collection<U> batchValues) {
        DbContext.batchUpdate(namedSQL, batchValues);
    }

    @Override
    public <U> void batchUpdate(String namedSQL, Map<String, ?>[] batchValues) {
        DbContext.batchUpdate(namedSQL, batchValues);
    }

    @Override
    public T update(T instance) {
        return jdbcAggregateTemplate.update(instance);
    }

    @Override
    public Iterable<T> updateAll(Iterable<T> instances) {
        return jdbcAggregateTemplate.updateAll(instances);
    }

    @Override
    public long count(Query query) {
        return jdbcAggregateTemplate.count(query, entity.getType());
    }

    @Override
    public boolean exists(Query query) {
        return jdbcAggregateTemplate.exists(query, entity.getType());
    }

    @Override
    public Optional<T> findOne(Query query) {
        return jdbcAggregateTemplate.findOne(query, entity.getType());
    }

    @Override
    public Iterable<T> findAll(Query query) {
        return jdbcAggregateTemplate.findAll(query, entity.getType());
    }

    @Override
    public Page<T> findAll(Query query, Pageable pageable) {
        return jdbcAggregateTemplate.findAll(query, entity.getType(), pageable);
    }

    @Override
    public long count(Map<String, Object> simpleMap) {
        return jdbcAggregateTemplate.count(Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap)), entity.getType());
    }

    @Override
    public boolean exists(Map<String, Object> simpleMap) {
        return jdbcAggregateTemplate.exists(Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap)), entity.getType());
    }

    @Override
    public Optional<T> findOne(Map<String, Object> simpleMap) {
        return jdbcAggregateTemplate.findOne(Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap)), entity.getType());
    }

    @Override
    public Optional<T> findOne(Map<String, Object> simpleMap, Sort sort) {
        Query query = Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap));
        return jdbcAggregateTemplate.findOne(query.sort(sort), entity.getType());
    }

    @Override
    public Iterable<T> findAll(Map<String, Object> simpleMap) {
        return jdbcAggregateTemplate.findAll(Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap)), entity.getType());
    }

    @Override
    public Iterable<T> findAll(Map<String, Object> simpleMap, Sort sort) {
        Query query = Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap));
        return jdbcAggregateTemplate.findAll(query.sort(sort), entity.getType());
    }

    @Override
    public Page<T> findAll(Map<String, Object> simpleMap, Pageable pageable) {
        return jdbcAggregateTemplate.findAll(Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap)), entity.getType(), pageable);
    }
}
