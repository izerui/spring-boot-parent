package com.yj2025.jdbc;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author liuyuhua
 * @date 2023年07月12日
 */
@NoRepositoryBean
public interface PlatformJdbcRepository<T, ID> extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {

    int readTimeoutSeconds = 10;

    /**
     * 专用插入功能。如果聚合根是新的并进行插入，则跳过测试。
     * <p>
     * 如果客户端为新的聚合根提供了一个id，将使用新的id。
     * </p>
     *
     * @param instance 要插入的聚合根实例。 不能为空
     * @return 返回保存后的实例.
     */
    T insert(T instance);

    /**
     * 插入所有聚合实例，包括每个聚合实例的所有成员。
     * <p>
     * 如果客户端为新的聚合根提供了一个id，将使用新的id。
     * </p>
     *
     * @param instances 要插入的聚合根实例。 不能为空
     * @return 返回保存后的实例.
     * @since 3.1
     */
    Iterable<T> insertAll(Iterable<T> instances);

    /**
     * 批量插入,效率高
     *
     * @param instances     要插入的实例集合。 不能为空
     * @param generatedKeys 自动生成的列
     */
    void batchInsert(Collection<T> instances, String... generatedKeys);

    /**
     * 通过命名SQL执行批量插入或者更新
     *
     * @param namedSQL    命名SQL 变量使用类似 `update test_user set age = 18 where id = :id`
     * @param batchValues value数组
     * @param <U>         任何对象或者map对象
     */
    <U> void batchUpdate(String namedSQL, Collection<U> batchValues);

    /**
     * 通过命名SQL执行批量插入或者更新
     *
     * @param namedSQL    命名SQL 变量使用类似 `update test_user set age = 18 where id = :id`
     * @param batchValues Map数组
     * @param <U>         map对象数组
     */
    <U> void batchUpdate(String namedSQL, Map<String, ?>[] batchValues);

    /**
     * 专用更新功能。这将跳过聚合根是否为新根的测试，并始终执行更新操作。
     *
     * @param instance 要更新的聚合根实例. 不能为空
     * @return 返回更新后的实例
     */
    T update(T instance);

    /**
     * 更新所有聚合实例，包括每个聚合实例的所有成员。
     *
     * @param instances 要更新的聚合根实例. 不能为空
     * @return 返回更新后的实例
     * @since 3.1
     */
    Iterable<T> updateAll(Iterable<T> instances);

    /**
     * 根据<code>查询条件<code>匹配聚合计数。
     *
     * @param query 查询条件，不能为空
     * @return 数据库中存储的实例数量。不能为空
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    long count(Query query);

    /**
     * 确定是否有与{@link Query}匹配的聚合
     *
     * @param query 查询条件，不能为空
     * @return {@literal true} 如果对象存在.
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    boolean exists(Query query);


    /**
     * 执行{@code SELECT}查询并将结果项转换为确保一个结果的实体。
     *
     * @param query 查询条件，不能为空
     * @return 如果没有找到匹配项，则为{@link Optional#empty()}。
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException 如果找到多个.
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Optional<T> findOne(Query query);

    /**
     * 执行{@code SELECT}查询并将结果项转换为已排序的{@link Iterable}。
     *
     * @param query 查询条件，不能为空
     * @return 包含所有匹配结果的非空排序列表。
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Iterable<T> findAll(Query query);

    /**
     * 返回一个{@link Page}的实体，匹配给定的{@link Query}。
     * 如果找不到匹配项，则返回一个空{@link Page}。
     *
     * @param query    查询条件，不能为空
     * @param pageable 分页对象，不能为空
     * @return 返回匹配给定 {@link Query} 条件的Page对象
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Page<T> findAll(Query query, Pageable pageable);

    /**
     * 根据<code>查询条件<code>匹配聚合计数。
     *
     * @param simpleMap 查询条件，不能为空
     * @return 数据库中存储的实例数量。不能为空
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    long count(Map<String, Object> simpleMap);

    /**
     * 确定是否有与{@link Query}匹配的聚合
     *
     * @param simpleMap 查询条件，不能为空
     * @return {@literal true} 如果对象存在.
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    boolean exists(Map<String, Object> simpleMap);

    /**
     * 执行{@code SELECT}查询并将结果项转换为确保一个结果的实体。
     *
     * @param simpleMap 查询条件，不能为空
     * @return 如果没有找到匹配项，则为{@link Optional#empty()}。
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException 如果找到多个.
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Optional<T> findOne(Map<String, Object> simpleMap);

    /**
     * 执行{@code SELECT}查询并将结果项转换为确保一个结果的实体。
     *
     * @param simpleMap 查询条件，不能为空
     * @return 如果没有找到匹配项，则为{@link Optional#empty()}。
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException 如果找到多个.
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Optional<T> findOne(Map<String, Object> simpleMap, Sort sort);

    /**
     * 执行{@code SELECT}查询并将结果项转换为已排序的{@link Iterable}。
     *
     * @param simpleMap 查询条件，不能为空
     * @return 包含所有匹配结果的非空排序列表。
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Iterable<T> findAll(Map<String, Object> simpleMap);

    /**
     * 执行{@code SELECT}查询并将结果项转换为已排序的{@link Iterable}。
     *
     * @param simpleMap 查询条件，不能为空
     * @return 包含所有匹配结果的非空排序列表。
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Iterable<T> findAll(Map<String, Object> simpleMap, Sort sort);

    /**
     * 返回一个{@link Page}的实体，匹配给定的{@link Query}。
     * 如果找不到匹配项，则返回一个空{@link Page}。
     *
     * @param simpleMap 查询条件，不能为空
     * @param pageable  分页对象，不能为空
     * @return 返回匹配给定 {@link Query} 条件的Page对象
     * @since 3.0
     */
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Page<T> findAll(Map<String, Object> simpleMap, Pageable pageable);

    @Deprecated(since = "3.1", forRemoval = true)
    @Override
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Iterable<T> findAll();

    /**
     * 共享锁、排它锁参考: https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.locking
     * @param id must not be {@literal null}.
     * @return
     */
    @Override
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Optional<T> findById(ID id);

    @Override
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Iterable<T> findAllById(Iterable<ID> ids);

    @Deprecated(since = "3.1", forRemoval = true)
    @Override
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Iterable<T> findAll(Sort sort);

    @Deprecated(since = "3.1", forRemoval = true)
    @Override
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    Page<T> findAll(Pageable pageable);

    @Deprecated(since = "3.1", forRemoval = true)
    @Override
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    long count();

    @Override
    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    boolean existsById(ID id);

    @Deprecated(since = "3.1", forRemoval = true)
    @Override
    void deleteAll();

    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    @Override
    <S extends T> Optional<S> findOne(Example<S> example);

    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    @Override
    <S extends T> Iterable<S> findAll(Example<S> example);

    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    @Override
    <S extends T> Iterable<S> findAll(Example<S> example, Sort sort);

    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    @Override
    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    @Override
    <S extends T> long count(Example<S> example);

    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    @Override
    <S extends T> boolean exists(Example<S> example);

    @Transactional(timeout = readTimeoutSeconds, readOnly = true)
    @Override
    <S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
}
