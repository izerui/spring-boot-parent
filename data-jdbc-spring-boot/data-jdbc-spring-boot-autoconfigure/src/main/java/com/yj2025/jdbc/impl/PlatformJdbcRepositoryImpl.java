package com.yj2025.jdbc.impl;

import com.google.common.collect.Lists;
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
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public T findOne(Query query) {
        return jdbcAggregateTemplate.findOne(query, entity.getType()).orElse(null);
    }

    @Override
    public Optional<T> getOne(Query query) {
        return jdbcAggregateTemplate.findOne(query, entity.getType());
    }

    @Override
    public List<T> findAll(Query query) {
        return Lists.newArrayList(jdbcAggregateTemplate.findAll(query, entity.getType()));
    }

    @Override
    public List<T> findAll(Query query, Sort sort) {
        return Lists.newArrayList(jdbcAggregateTemplate.findAll(query.sort(sort), entity.getType()));
    }

    /**
     * 1. findAll:145, PlatformJdbcRepositoryImpl (com.yj2025.jdbc.impl)
     * 2. findAll:333, JdbcAggregateTemplate (org.springframework.data.jdbc.core)
     * 3. getPage:63, PageableExecutionUtils (org.springframework.data.support)
     * 4. lambda$findAll$5:333, JdbcAggregateTemplate (org.springframework.data.jdbc.core)
     * 5. count:384, DefaultDataAccessStrategy (org.springframework.data.jdbc.core.convert)
     * 6. sql:415, DefaultDataAccessStrategy (org.springframework.data.jdbc.core.convert)
     * 7. getSqlGenerator:63, SqlGeneratorSource (org.springframework.data.jdbc.core.convert)
     * @param query    查询条件，不能为空
     * @param pageable 分页对象，不能为空
     * @return
     */
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
    public T findOne(Map<String, Object> simpleMap) {
        return jdbcAggregateTemplate.findOne(Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap)), entity.getType()).orElse(null);
    }

    @Override
    public T findOne(Map<String, Object> simpleMap, Sort sort) {
        Query query = Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap));
        return jdbcAggregateTemplate.findOne(query.sort(sort), entity.getType()).orElse(null);
    }

    @Override
    public List<T> findAll(Map<String, Object> simpleMap) {
        Iterable<T> iterable = jdbcAggregateTemplate.findAll(Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap)), entity.getType());
        return Lists.newArrayList(iterable);
    }

    @Override
    public List<T> findAll(Map<String, Object> simpleMap, Sort sort) {
        Query query = Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap));
        return Lists.newArrayList(jdbcAggregateTemplate.findAll(query.sort(sort), entity.getType()));
    }

    @Override
    public Page<T> findAll(Map<String, Object> simpleMap, Pageable pageable) {
        return jdbcAggregateTemplate.findAll(Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap)), entity.getType(), pageable);
    }

    @Override
    public <S> S aggregate(Collection<String> aggregateColumns, Class<S> mappingClass, Query query) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = generator.getSelectWhereSql(aggregateColumns, query, parameterSource);
        if (query.isSorted()) {
            List<String> orderList = query.getSort().stream().map(order -> order.getProperty() + " " + order.getDirection().name()).collect(Collectors.toList());
            sql += " order by " + StringUtils.join(orderList, ",") + " ";
        }
        if (Map.class.isAssignableFrom(mappingClass)) {
            return (S) namedParameterJdbcTemplate.queryForObject(sql, parameterSource, new ColumnMapRowMapper());
        }
        return namedParameterJdbcTemplate.queryForObject(sql, parameterSource, new BeanPropertyRowMapper<>(mappingClass));
    }

    @Override
    public <S> S aggregate(Collection<String> aggregateColumns, Class<S> mappingClass, Map<String, Object> simpleMap) {
        Query query = Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap));
        return this.aggregate(aggregateColumns, mappingClass, query);
    }

    @Override
    public <S> List<S> groupAll(Collection<String> selectColumns, @Nullable Collection<String> groupColumns, Class<S> mappingClass, Query query) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = generator.getGroupSql(selectColumns, groupColumns, query, parameterSource);
        if (query.isSorted()) {
            List<String> orderList = query.getSort().stream().map(order -> order.getProperty() + " " + order.getDirection().name()).collect(Collectors.toList());
            sql += " order by " + StringUtils.join(orderList, ",") + " ";
        }
        if (Map.class.isAssignableFrom(mappingClass)) {
            return (List<S>) namedParameterJdbcTemplate.query(sql, parameterSource, new ColumnMapRowMapper());
        }
        return namedParameterJdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(mappingClass));
    }

    @Override
    public <S> Page<S> groupAll(Collection<String> selectColumns, @Nullable Collection<String> groupColumns, Class<S> mappingClass, Query query, Pageable pageable) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = generator.getGroupSql(selectColumns, groupColumns, query, parameterSource);
        String pageSql = "select x.* from (" + sql + ") x ";
        String countSql = "select count(0) from (" + sql + ") x";
        if (pageable.getSort().isSorted()) {
            List<String> orderList = pageable.getSort().stream()
                    .map(order -> "x." + CriteriaUtils.camelToUnderscore(order.getProperty()) + " " + order.getDirection().name())
                    .collect(Collectors.toList());
            pageSql += " order by " + StringUtils.join(orderList, ",") + " ";
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
    public <S> List<S> groupAll(Collection<String> selectColumns, @Nullable Collection<String> groupColumns, Class<S> mappingClass, Map<String, Object> simpleMap) {
        Query query = Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(), simpleMap));
        return this.groupAll(selectColumns, groupColumns, mappingClass, query);
    }

    @Override
    public <S> Page<S> groupAll(Collection<String> selectColumns, @Nullable Collection<String> groupColumns, Class<S> mappingClass, Map<String, Object> simpleMap, Pageable pageable) {
        Query query =
                Query.query(CriteriaUtils.joinToCriteria(Criteria.empty(),
                        simpleMap));
        return this.groupAll(selectColumns, groupColumns, mappingClass, query
                , pageable);
    }

    @Override
    public T findByRecordId(String entCode, String recordId) {
        Query query = Query.query(
                Criteria.where("ent_code").is(entCode).and("record_id").is(recordId)
        );
        return jdbcAggregateTemplate.findOne(query, entity.getType()).orElse(null);
    }

    @Override
    public Optional<T> getByRecordId(String entCode, String recordId) {
        Query query = Query.query(
                Criteria.where("ent_code").is(entCode).and("record_id").is(recordId)
        );
        return jdbcAggregateTemplate.findOne(query, entity.getType());
    }

    @Override
    public List<T> findByRecordIds(String entCode, Iterable<String> recordIds) {
        Query query = Query.query(
                Criteria.where("ent_code").is(entCode).and("record_id").in(recordIds)
        );
        return Lists.newArrayList(jdbcAggregateTemplate.findAll(query,
                entity.getType()));
    }
}
