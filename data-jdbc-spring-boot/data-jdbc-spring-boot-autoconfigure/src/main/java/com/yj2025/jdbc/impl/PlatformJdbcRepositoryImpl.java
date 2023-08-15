package com.yj2025.jdbc.impl;

import com.yj2025.basic.support.DbContext;
import com.yj2025.jdbc.PlatformJdbcRepository;
import com.yj2025.jdbc.support.CriteriaUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.SqlGeneratorSource;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author liuyuhua
 * @date 2023年07月12日
 */
public class PlatformJdbcRepositoryImpl<T, ID> extends SimpleJdbcRepository<T, ID> implements PlatformJdbcRepository<T, ID> {

    private final JdbcAggregateTemplate jdbcAggregateTemplate;
    private final PersistentEntity<T, ?> entity;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final SqlGeneratorSource sqlGeneratorSource;
    private final Dialect dialect;
    private final JdbcConverter jdbcConverter;
    private final RelationalMappingContext relationalMappingContext;
    private final CustomSqlGenerator generator;

    public PlatformJdbcRepositoryImpl(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity, JdbcConverter converter) throws IllegalAccessException {
        super(entityOperations, entity, converter);
        this.jdbcAggregateTemplate = (JdbcAggregateTemplate) entityOperations;
        this.entity = entity;
        this.jdbcConverter = converter;
        DefaultDataAccessStrategy dataAccessStrategy = getRefFieldValue(this.jdbcAggregateTemplate, "accessStrategy");
        this.namedParameterJdbcTemplate = getRefFieldValue(dataAccessStrategy, "operations");
        this.sqlGeneratorSource = getRefFieldValue(dataAccessStrategy, "sqlGeneratorSource");
        this.relationalMappingContext = getRefFieldValue(dataAccessStrategy, "context");
        this.dialect = getRefFieldValue(sqlGeneratorSource, "dialect");
        this.jdbcTemplate = namedParameterJdbcTemplate.getJdbcTemplate();
        this.generator = new CustomSqlGenerator(relationalMappingContext, jdbcConverter, relationalMappingContext.getRequiredPersistentEntity(entity.getType()), dialect);
    }


    private <S> S getRefFieldValue(Object delegatedTarget, String propertyName) throws IllegalAccessException {
        Field field = ReflectionUtils.findField(delegatedTarget.getClass(), propertyName);
        field.setAccessible(true);
        return (S) field.get(delegatedTarget);
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

//    @Override
//    public <S> S group(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Query query) {
//        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
//        String sql = generator.getGroupSql(selectColumns, groupColumns, query, parameterSource);
//        if (Map.class.isAssignableFrom(mappingClass)) {
//            return (S) namedParameterJdbcTemplate.query(sql, parameterSource, new ColumnMapRowMapper());
//        }
//        return namedParameterJdbcTemplate.queryForObject(sql,parameterSource,mappingClass);
//    }
//
//    @Override
//    public <S> S group(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Map<String, Object> simpleMap) {
//        Query query = Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap));
//        return this.group(selectColumns,groupColumns,mappingClass,query);
//    }


    @Override
    public <S> S aggregate(Collection<String> aggregateColumns, Class<S> mappingClass, Query query) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = generator.getSelectWhereSql(aggregateColumns, query, parameterSource);
        if (Map.class.isAssignableFrom(mappingClass)) {
            return (S) namedParameterJdbcTemplate.query(sql, parameterSource, new ColumnMapRowMapper()).get(0);
        }
        return namedParameterJdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(mappingClass)).get(0);
    }

    @Override
    public <S> S aggregate(Collection<String> aggregateColumns, Class<S> mappingClass, Map<String, Object> simpleMap) {
        Query query = Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap));
        return this.aggregate(aggregateColumns, mappingClass, query);
    }

    @Override
    public <S> List<S> groupAll(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Query query) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = generator.getGroupSql(selectColumns, groupColumns, query, parameterSource);
        if (Map.class.isAssignableFrom(mappingClass)) {
            return (List<S>) namedParameterJdbcTemplate.query(sql, parameterSource, new ColumnMapRowMapper());
        }
        return namedParameterJdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(mappingClass));
    }

    @Override
    public <S> Page<S> groupAll(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Query query, Pageable pageable) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = generator.getGroupSql(selectColumns, groupColumns, query, parameterSource);
        String pageSql = "select x.* from (" + sql + ") x ";
        String countSql = "select count(0) from (" + sql + ") x";
        if (pageable.getSort().isSorted()) {
            pageSql += " order by " + StringUtils.join(pageable.getSort().stream().map(order -> "x." + order.getProperty() + " " + order.getDirection().name()), ",");
        }
        pageSql += dialect.limit().getLimitOffset(pageable.getPageSize(), pageable.getOffset());
        List<S> content;
        if (Map.class.isAssignableFrom(mappingClass)) {
            content = (List<S>) namedParameterJdbcTemplate.query(pageSql, parameterSource, new ColumnMapRowMapper());
        } else {
            content = namedParameterJdbcTemplate.query(pageSql, parameterSource, new BeanPropertyRowMapper<>(mappingClass));
        }
        return PageableExecutionUtils.getPage(content, pageable, () -> namedParameterJdbcTemplate.queryForObject(countSql, parameterSource, Long.class));
    }

    @Override
    public <S> List<S> groupAll(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Map<String, Object> simpleMap) {
        Query query = Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap));
        return this.groupAll(selectColumns, groupColumns, mappingClass, query);
    }

    @Override
    public <S> Page<S> groupAll(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Map<String, Object> simpleMap, Pageable pageable) {
        Query query = Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap));
        return this.groupAll(selectColumns, groupColumns, mappingClass, query, pageable);
    }

    @Override
    public Optional<T> findByRecordId(String recordId) {
        Query query = Query.query(
                Criteria.where("record_id").is(recordId)
        );
        return jdbcAggregateTemplate.findOne(query, entity.getType());
    }

    @Override
    public Iterable<T> findByRecordIds(Iterable<String> recordIds) {
        Query query = Query.query(
                Criteria.where("record_id").in(recordIds)
        );
        return jdbcAggregateTemplate.findAll(query, entity.getType());
    }
}
