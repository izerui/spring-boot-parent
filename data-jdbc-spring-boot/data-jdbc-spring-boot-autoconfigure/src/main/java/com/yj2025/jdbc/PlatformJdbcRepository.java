package com.yj2025.jdbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author liuyuhua
 * @date 2023年07月12日
 */
@NoRepositoryBean
@Transactional(readOnly = true)
public interface PlatformJdbcRepository<T, ID> extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {

    /**
     * 专用插入功能。如果聚合根是新的并进行插入，则跳过测试。
     * <p>
     * 如果客户端为新的聚合根提供了一个id，将使用新的id。
     * </p>
     *
     * @param instance 要插入的聚合根实例。 不能为空
     * @return 返回保存后的实例.
     */
    @Modifying
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
    @Modifying
    Iterable<T> insertAll(Iterable<T> instances);

    /**
     * 批量插入,效率高
     *
     * @param instances     要插入的实例集合。 不能为空
     * @param generatedKeys 自动生成的列
     */
    @Modifying
    void batchInsert(Collection<T> instances, String... generatedKeys);

    /**
     * 通过命名SQL执行批量插入或者更新
     *
     * @param namedSQL    命名SQL 变量使用类似 `update test_user set age = 18 where id = :id`
     * @param batchValues value数组
     * @param <U>         任何对象或者map对象
     */
    @Modifying
    <U> void batchUpdate(String namedSQL, Collection<U> batchValues);

    /**
     * 通过命名SQL执行批量插入或者更新
     *
     * @param namedSQL    命名SQL 变量使用类似 `update test_user set age = 18 where id = :id`
     * @param batchValues Map数组
     * @param <U>         map对象数组
     */
    @Modifying
    <U> void batchUpdate(String namedSQL, Map<String, ?>[] batchValues);

    /**
     * 专用更新功能。这将跳过聚合根是否为新根的测试，并始终执行更新操作。
     *
     * @param instance 要更新的聚合根实例. 不能为空
     * @return 返回更新后的实例
     */
    @Modifying
    T update(T instance);

    /**
     * 更新所有聚合实例，包括每个聚合实例的所有成员。
     *
     * @param instances 要更新的聚合根实例. 不能为空
     * @return 返回更新后的实例
     * @since 3.1
     */
    @Modifying
    Iterable<T> updateAll(Iterable<T> instances);

    /**
     * 根据<code>查询条件<code>匹配聚合计数。
     *
     * @param query 查询条件，不能为空
     * @return 数据库中存储的实例数量。不能为空
     * @since 3.0
     */
    long count(Query query);

    /**
     * 确定是否有与{@link Query}匹配的聚合
     *
     * @param query 查询条件，不能为空
     * @return {@literal true} 如果对象存在.
     * @since 3.0
     */
    boolean exists(Query query);


    /**
     * 执行{@code SELECT}查询并将结果项转换为确保一个结果的实体。
     *
     * @param query 查询条件，不能为空
     * @return 如果没有找到匹配项，则为{@link Optional#empty()}。
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException 如果找到多个.
     * @since 3.0
     */
    Optional<T> findOne(Query query);

    /**
     * 执行{@code SELECT}查询并将结果项转换为已排序的{@link Iterable}。
     *
     * @param query 查询条件，不能为空
     * @return 包含所有匹配结果的非空排序列表。
     * @since 3.0
     */
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
    Page<T> findAll(Query query, Pageable pageable);

    /**
     * 根据<code>查询条件<code>匹配聚合计数。
     *
     * @param simpleMap 查询条件，不能为空
     * @return 数据库中存储的实例数量。不能为空
     * @since 3.0
     */
    long count(Map<String, Object> simpleMap);

    /**
     * 确定是否有与{@link Query}匹配的聚合
     *
     * @param simpleMap 查询条件，不能为空
     * @return {@literal true} 如果对象存在.
     * @since 3.0
     */
    boolean exists(Map<String, Object> simpleMap);

    /**
     * 执行{@code SELECT}查询并将结果项转换为确保一个结果的实体。
     *
     * @param simpleMap 查询条件，不能为空
     * @return 如果没有找到匹配项，则为{@link Optional#empty()}。
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException 如果找到多个.
     * @since 3.0
     */
    Optional<T> findOne(Map<String, Object> simpleMap);

    /**
     * 执行{@code SELECT}查询并将结果项转换为确保一个结果的实体。
     *
     * @param simpleMap 查询条件，不能为空
     * @return 如果没有找到匹配项，则为{@link Optional#empty()}。
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException 如果找到多个.
     * @since 3.0
     */
    Optional<T> findOne(Map<String, Object> simpleMap, Sort sort);

    /**
     * 执行{@code SELECT}查询并将结果项转换为已排序的{@link Iterable}。
     *
     * @param simpleMap 查询条件，不能为空
     * @return 包含所有匹配结果的非空排序列表。
     * @since 3.0
     */
    Iterable<T> findAll(Map<String, Object> simpleMap);

    /**
     * 执行{@code SELECT}查询并将结果项转换为已排序的{@link Iterable}。
     *
     * @param simpleMap 查询条件，不能为空
     * @return 包含所有匹配结果的非空排序列表。
     * @since 3.0
     */
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
    Page<T> findAll(Map<String, Object> simpleMap, Pageable pageable);

    @Deprecated(since = "3.1", forRemoval = true)
    @Override
    Iterable<T> findAll();

    @Deprecated(since = "3.1", forRemoval = true)
    @Override
    Iterable<T> findAll(Sort sort);

    @Deprecated(since = "3.1", forRemoval = true)
    @Override
    Page<T> findAll(Pageable pageable);

    @Deprecated(since = "3.1", forRemoval = true)
    @Override
    long count();

    @Deprecated(since = "3.1", forRemoval = true)
    @Override
    void deleteAll();

    <S> S group(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Query query);

    <S> List<S> groupAll(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Query query);

    <S> List<S> groupAll(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Map<String,Object> simpleMap);

    <S> Page<S> groupAll(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Query query, Pageable pageable);

    <S> Page<S> groupAll(Collection<String> selectColumns, Collection<String> groupColumns, Class<S> mappingClass, Map<String,Object> simpleMap, Pageable pageable);

    Optional<T> findByRecordId(String recordId);

    Iterable<T> findByRecordIds(Iterable<String> recordIds);

}
