package com.yj2025.jdbc;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

/**
 * @author liuyuhua
 * @date 2023年07月12日
 */
@NoRepositoryBean
public interface PlatformJdbcRepository<T, ID> extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {


    /**
     * Dedicated insert function. This skips the test if the aggregate root is new and makes an insert.
     * <p>
     * This is useful if the client provides an id for new aggregate roots.
     * </p>
     *
     * @param instance the aggregate root of the aggregate to be inserted. Must not be {@code null}.
     * @return the saved instance.
     */
    T insert(T instance);

    /**
     * Inserts all aggregate instances, including all the members of each aggregate instance.
     * <p>
     * This is useful if the client provides an id for new aggregate roots.
     * </p>
     *
     * @param instances the aggregate roots to be inserted. Must not be {@code null}.
     * @return the saved instances.
     * @since 3.1
     */
    Iterable<T> insertAll(Iterable<T> instances);

    /**
     * Dedicated update function. This skips the test if the aggregate root is new or not and always performs an update
     * operation.
     *
     * @param instance the aggregate root of the aggregate to be inserted. Must not be {@code null}.
     * @return the saved instance.
     */
    T update(T instance);

    /**
     * Updates all aggregate instances, including all the members of each aggregate instance.
     *
     * @param instances the aggregate roots to be inserted. Must not be {@code null}.
     * @return the saved instances.
     * @since 3.1
     */
    Iterable<T> updateAll(Iterable<T> instances);

    /**
     * Counts the number of aggregates of a given type that match the given <code>query</code>.
     *
     * @param query must not be {@literal null}.
     * @return the number of instances stored in the database. Guaranteed to be not {@code null}.
     * @since 3.0
     */
    long count(Query query);

    /**
     * Determine whether there are aggregates that match the {@link Query}
     *
     * @param query must not be {@literal null}.
     * @return {@literal true} if the object exists.
     * @since 3.0
     */
    boolean exists(Query query);


    /**
     * Execute a {@code SELECT} query and convert the resulting item to an entity ensuring exactly one result.
     *
     * @param query must not be {@literal null}.
     * @return exactly one result or {@link Optional#empty()} if no match found.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if more than one match found.
     * @since 3.0
     */
    Optional<T> findOne(Query query);

    /**
     * Execute a {@code SELECT} query and convert the resulting items to a {@link Iterable} that is sorted.
     *
     * @param query must not be {@literal null}.
     * @return a non-null sorted list with all the matching results.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if more than one match found.
     * @since 3.0
     */
    Iterable<T> findAll(Query query);

    /**
     * Returns a {@link Page} of entities matching the given {@link Query}. In case no match could be found, an empty
     * {@link Page} is returned.
     *
     * @param query    must not be {@literal null}.
     * @param pageable can be null.
     * @return a {@link Page} of entities matching the given {@link Example}.
     * @since 3.0
     */
    Page<T> findAll(Query query, Pageable pageable);

}
