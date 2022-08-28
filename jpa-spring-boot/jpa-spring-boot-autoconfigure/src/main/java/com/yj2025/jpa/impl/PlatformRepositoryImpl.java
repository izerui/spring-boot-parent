/**
 * Copyright (C) 2014 serv (liuyuhua69@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.yj2025.jpa.impl;

import com.yj2025.jpa.PlatformJpaRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by serv on 14-5-29.
 */
public class PlatformRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements PlatformJpaRepository<T, ID> {

    private final EntityManager entityManager;
    private final JpaEntityInformation<T, ?> entityInformation;
    private final Map<Class, JpaEntityInformation<?, ?>> entityInformationMap = new HashMap<>();

    public PlatformRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    public PlatformRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
    }

    @Override
    public void batchInsert(Iterable<T> inserts) {
        for (T t : inserts) {
            entityManager.persist(t);
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public void batchUpdate(Iterable<T> updates) {
        for (T t : updates) {
            entityManager.merge(t);
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public T findOne(ID id) {
        Optional<T> optional = this.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public T findOne(Conditions conditions) {
        Assert.notNull(conditions, "条件不能为空");
        List<T> list = (List<T>) new JpqlQueryHolder(conditions).createQuery().getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public T findOne(Conditions conditions, Sort sort) {
        Assert.notNull(conditions, "条件不能为空");
        List<T> list = (List<T>) new JpqlQueryHolder(conditions, sort).createQuery().getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<T> findAll(Conditions conditions) {
        return new JpqlQueryHolder(conditions).createQuery().getResultList();
    }

    @Override
    public List<?> findAll(JpqlSelector selector) {
        return new JpqlQueryHolder(selector.conditions).createQuery(selector.fields, selector.tables).getResultList();
    }

    @Override
    public List<?> findAll(JpqlSelector selector, Sort sort) {
        return new JpqlQueryHolder(selector.conditions, sort).createQuery(selector.fields, selector.tables).getResultList();
    }

    @Override
    public List<T> findAll(Conditions conditions, Sort sort) {
        return new JpqlQueryHolder(conditions, sort).createQuery().getResultList();
    }

    @Override
    public Page<T> findAll(Conditions conditions, Pageable pageable) {
        return findPage(conditions, pageable);
    }

    @Override
    public Page<T> findPage(Conditions conditions, Pageable pageable) {
        if (pageable == null) {
            return new PageImpl<T>((List<T>) findAll(conditions));
        }

        Long total = count(conditions);

        Query query = new JpqlQueryHolder(conditions, pageable.getSort()).createQuery();

        query.setFirstResult(Long.valueOf(pageable.getOffset()).intValue());
        query.setMaxResults(pageable.getPageSize());

        List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.<T>emptyList();

        return new PageImpl<T>(content, pageable, total);
    }


    @Override
    public <R> List<R> groupAll(List<String> selectFields, List<String> groupFields, Class<R> rClass) {
        return this.groupAll(selectFields, groupFields, rClass, -1);
    }

    @Override
    public <R> List<R> groupAll(List<String> selectFields, List<String> groupFields, Class<R> rClass, int limit) {
        Assert.notEmpty(selectFields);
        Assert.notEmpty(groupFields);
        List<Map> mapList = new JpqlQueryHolder().createGroupQuery(selectFields, groupFields, limit).getResultList();
        if (rClass.isAssignableFrom(Map.class)) {
            return (List<R>) mapList;
        } else {
            return transMap2Bean(mapList, rClass);
        }
    }


    @Override
    public <R> List<R> groupAll(Sort sort, List<String> selectFields, List<String> groupFields, Class<R> rClass) {
        return this.groupAll(sort, selectFields, groupFields, rClass, -1);
    }

    @Override
    public <R> List<R> groupAll(Sort sort, List<String> selectFields, List<String> groupFields, Class<R> rClass, int limit) {
        Assert.notEmpty(selectFields);
        Assert.notEmpty(groupFields);
        List<Map> mapList = new JpqlQueryHolder(sort).createGroupQuery(selectFields, groupFields, limit).getResultList();
        if (rClass.isAssignableFrom(Map.class)) {
            return (List<R>) mapList;
        } else {
            return transMap2Bean(mapList, rClass);
        }
    }

    @Override
    public <R> List<R> groupAll(Conditions conditions, List<String> selectFields, List<String> groupFields, Class<R> rClass) {
        return this.groupAll(conditions, selectFields, groupFields, rClass, -1);
    }

    @Override
    public <R> List<R> groupAll(Conditions conditions, List<String> selectFields, List<String> groupFields, Class<R> rClass, int limit) {
        Assert.notEmpty(selectFields);
        Assert.notEmpty(groupFields);
        List<Map> mapList = new JpqlQueryHolder(conditions).createGroupQuery(selectFields, groupFields, limit).getResultList();
        if (rClass.isAssignableFrom(Map.class)) {
            return (List<R>) mapList;
        } else {
            return transMap2Bean(mapList, rClass);
        }
    }

    @Override
    public <R> List<R> groupAll(Conditions conditions, Sort sort, List<String> selectFields, List<String> groupFields, Class<R> rClass) {
        return this.groupAll(conditions, sort, selectFields, groupFields, rClass, -1);
    }

    @Override
    public <R> List<R> groupAll(Conditions conditions, Sort sort, List<String> selectFields, List<String> groupFields, Class<R> rClass, int limit) {
        Assert.notEmpty(selectFields);
        Assert.notEmpty(groupFields);
        List<Map> mapList = new JpqlQueryHolder(conditions, sort).createGroupQuery(selectFields, groupFields, limit).getResultList();
        if (rClass.isAssignableFrom(Map.class)) {
            return (List<R>) mapList;
        } else {
            return transMap2Bean(mapList, rClass);
        }
    }

    @Override
    public <R> Page<R> groupAll(Conditions conditions, Pageable pageable, List<String> selectFields, List<String> groupFields, Class<R> rClass) {
        return groupPage(conditions, pageable, selectFields, groupFields, rClass);
    }

    @Override
    public <R> Page<R> groupPage(Conditions conditions, Pageable pageable, List<String> selectFields, List<String> groupFields, Class<R> rClass) {
        Assert.notEmpty(selectFields);
        Assert.notEmpty(groupFields);
        Assert.notNull(pageable);
        Long total = count(conditions, "distinct " + StringUtils.join(groupFields, ","));

        Query query = new JpqlQueryHolder(conditions, pageable.getSort()).createGroupQuery(selectFields, groupFields);
        query.setFirstResult(Long.valueOf(pageable.getOffset()).intValue());
        query.setMaxResults(pageable.getPageSize());

        List<Map> content = total > pageable.getOffset() ? query.getResultList() : Collections.<Map>emptyList();
        if (rClass.isAssignableFrom(Map.class)) {
            return new PageImpl(content, pageable, total);
        } else {
            return new PageImpl(transMap2Bean(content, rClass), pageable, total);
        }
    }

    @Override
    public <R> List<R> distinctAll(Class<R> rClass) {
        List<String> fields = getFields(rClass);
        List<Map> mapList = new JpqlQueryHolder().createGroupQuery(fields, fields).getResultList();
        if (rClass.isAssignableFrom(Map.class)) {
            return (List<R>) mapList;
        } else {
            return transMap2Bean(mapList, rClass);
        }
    }


    @Override
    public <R> List<R> distinctAll(Sort sort, Class<R> rClass) {
        List<String> fields = getFields(rClass);
        List<Map> mapList = new JpqlQueryHolder(sort).createGroupQuery(fields, fields).getResultList();
        if (rClass.isAssignableFrom(Map.class)) {
            return (List<R>) mapList;
        } else {
            return transMap2Bean(mapList, rClass);
        }
    }

    @Override
    public <R> List<R> distinctAll(Conditions conditions, Class<R> rClass) {
        List<String> fields = getFields(rClass);
        List<Map> mapList = new JpqlQueryHolder(conditions).createGroupQuery(fields, fields).getResultList();
        if (rClass.isAssignableFrom(Map.class)) {
            return (List<R>) mapList;
        } else {
            return transMap2Bean(mapList, rClass);
        }
    }

    @Override
    public <R> List<R> distinctAll(Conditions conditions, Sort sort, Class<R> rClass) {
        List<String> fields = getFields(rClass);
        List<Map> mapList = new JpqlQueryHolder(conditions, sort).createGroupQuery(fields, fields).getResultList();
        if (rClass.isAssignableFrom(Map.class)) {
            return (List<R>) mapList;
        } else {
            return transMap2Bean(mapList, rClass);
        }
    }

    @Override
    public <R> Page<R> distinctAll(Conditions conditions, Pageable pageable, Class<R> rClass) {
        return distinctPage(conditions, pageable, rClass);
    }

    @Override
    public <R> Page<R> distinctPage(Conditions conditions, Pageable pageable, Class<R> rClass) {
        List<String> fields = getFields(rClass);
        Assert.notNull(pageable);

//        Long total = count(conditions, "distinct " + StringUtils.join(fields, ","));
        List<R> list = groupAll(conditions, fields, fields, rClass);
        Long total = Long.valueOf(list.size() + "");

        Query query = new JpqlQueryHolder(conditions, pageable.getSort()).createGroupQuery(fields, fields);
        query.setFirstResult(Long.valueOf(pageable.getOffset()).intValue());
        query.setMaxResults(pageable.getPageSize());

        List<Map> content = total > pageable.getOffset() ? query.getResultList() : Collections.<Map>emptyList();
        if (rClass.isAssignableFrom(Map.class)) {
            return new PageImpl(content, pageable, total);
        } else {
            return new PageImpl(transMap2Bean(content, rClass), pageable, total);
        }
    }

    @Override
    public long count(Conditions conditions) {
        return this.count(conditions, JpqlQueryHolder.ALIAS);
    }

    @Override
    public long count(Conditions conditions, String columnName) {
        return this.aggregate(conditions, "count(" + columnName + ")", Long.class);
    }

    @Override
    public <R> R sum(String columnName, Class<R> resultClass) {
        return this.aggregate("sum(" + columnName + ")", resultClass);
    }

    @Override
    public <R> R sum(Conditions conditions, String columnName, Class<R> resultClass) {
        return this.aggregate(conditions, "sum(" + columnName + ")", resultClass);
    }

    @Override
    public <R> R max(String columnName, Class<R> resultClass) {
        return this.aggregate("max(" + columnName + ")", resultClass);
    }

    @Override
    public <R> R max(Conditions conditions, String columnName, Class<R> resultClass) {
        return this.aggregate(conditions, "max(" + columnName + ")", resultClass);
    }

    @Override
    public <R> R min(String columnName, Class<R> resultClass) {
        return this.aggregate("min(" + columnName + ")", resultClass);
    }

    @Override
    public <R> R min(Conditions conditions, String columnName, Class<R> resultClass) {
        return this.aggregate(conditions, "min(" + columnName + ")", resultClass);
    }

    @Override
    public <R> R avg(String columnName, Class<R> resultClass) {
        return this.aggregate("avg(" + columnName + ")", resultClass);
    }

    @Override
    public <R> R avg(Conditions conditions, String columnName, Class<R> resultClass) {
        return this.aggregate(conditions, "avg(" + columnName + ")", resultClass);
    }

    @Override
    public <R> R aggregate(Conditions conditions, String aggregate, Class<R> resultClass) {
        TypedQuery<R> query = new JpqlQueryHolder(conditions).createAggregateQuery(aggregate, resultClass);
        List<R> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public <R> R aggregate(String aggregate, Class<R> resultClass) {
        TypedQuery<R> query = new JpqlQueryHolder().createAggregateQuery(aggregate, resultClass);
        List<R> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void deleteAll(Conditions conditions) {
        new JpqlQueryHolder(conditions).createDeleteQuery().executeUpdate();
    }

    @Override
    public Class<T> getEntityClass() {
        return entityInformation.getJavaType();
    }

    @Override
    public List<?> selectSql(String sql, Conditions conditions) {
        Query query = new JpqlQueryHolder(conditions).createJpqlQuery(sql);
        return query.getResultList();
    }


    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
    private <R> List<R> transMap2Bean(List<Map> mapList, Class<R> tClass) {
        try {
            List<R> rList = new ArrayList<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(tClass);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (Map map : mapList) {
                R obj = tClass.newInstance();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();
                    if (map.containsKey(key)) {
                        Object value = map.get(key);
                        // 得到property对应的setter方法
                        Method setter = property.getWriteMethod();
                        setter.invoke(obj, value);
                    }
                }
                rList.add(obj);
            }
            return rList;
        } catch (Exception e) {
            throw new RuntimeException("jpa: map 2 bean error", e);
        }
    }

    private List<String> getFields(Class tClass) {
        try {
            List<String> fields = new ArrayList<>();
            if (tClass.isAssignableFrom(Map.class)) {
                throw new RuntimeException("不支持从Map获取字段列表");
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(tClass);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!property.getPropertyType().isAssignableFrom(Class.class)) {
                    fields.add(key);
                }
            }
            return fields;
        } catch (Exception e) {
            throw new RuntimeException("jpa: getFields error", e);
        }
    }


    private class JpqlQueryHolder {

        //别名
        private final static String ALIAS = "x";
        private final static String AGGREGATE_QUERY_STRING = "select %s from %s " + ALIAS;
        private final static String DELETE_QUERY_STRING = "delete from %s " + ALIAS;
        //QUERY ALL
        private final static String FIND_ALL_QUERY_STRING = "from %s " + ALIAS;

        private String condition;
        private Sort sort = Sort.unsorted();
        private Map params;

        private JpqlQueryHolder() {
        }

        private JpqlQueryHolder(Sort sort) {
            Assert.notNull(sort);
            this.sort = sort;
        }

        private JpqlQueryHolder(Conditions conditions) {
            this.params = new HashMap<>();
            this.condition = conditions.toQL(this.params);
        }

        private JpqlQueryHolder(Conditions conditions, Sort sort) {
            if (sort == null) {
                sort = Sort.unsorted();
            }
            this.params = new HashMap<>();
            this.condition = conditions.toQL(this.params);
            this.sort = sort;
        }

        private Query createQuery() {
            StringBuilder sb = new StringBuilder();
            // select x from table
            sb.append(QueryUtils.getQueryString(FIND_ALL_QUERY_STRING, entityInformation.getEntityName()))
                    //where
                    .append(applyCondition());
            Query query = entityManager.createQuery(QueryUtils.applySorting(sb.toString(), sort, ALIAS));
            applyQueryParameter(query);
            return query;
        }

        private Query createQuery(String fields, Map<Class, String> tables) {
            final AtomicReference<String> tablesQL = new AtomicReference<>("");
            tables.forEach((aClass, alias) -> {
                JpaEntityInformation<?, ?> information = entityInformationMap.get(aClass);
                if (information == null) {
                    information = JpaEntityInformationSupport.getEntityInformation(aClass, entityManager);
                    entityInformationMap.put(aClass, information);
                }
                String entityName = information.getEntityName();
                if (StringUtils.isEmpty(tablesQL.get())) {
                    tablesQL.set(entityName + " " + alias);
                } else {
                    tablesQL.set(tablesQL.get() + "," + entityName + " " + alias);
                }
            });
            StringBuilder sb = new StringBuilder();
            // select x from table
            sb.append("select ")
                    .append(fields)
                    .append(" from ")
                    .append(tablesQL.get())
                    //where
                    .append(applyCondition());
            Query query = entityManager.createQuery(QueryUtils.applySorting(sb.toString(), sort));
            applyQueryParameter(query);
            return query;
        }

        // 使用指定的sql执行查询
        private Query createJpqlQuery(String jpql) {
            StringBuilder sb = new StringBuilder();
            // select x from table
            sb.append(jpql)
                    //where
                    .append(applyCondition());
            Query query = entityManager.createQuery(QueryUtils.applySorting(sb.toString(), sort, ALIAS));
            applyQueryParameter(query);
            return query;
        }

        // aggregate 合计函数 例如： sum(age) count(x) max(age)...
        private <R> TypedQuery<R> createAggregateQuery(String aggregate, Class<R> resultClass) {
            String ql = String.format(AGGREGATE_QUERY_STRING, aggregate, entityInformation.getEntityName());
            ql += applyCondition();

            TypedQuery<R> query = entityManager.createQuery(ql, resultClass);
            applyQueryParameter(query);
            return query;
        }

        private Query createDeleteQuery() {
            String ql = String.format(DELETE_QUERY_STRING, entityInformation.getEntityName());
            ql += applyCondition();

            Query query = entityManager.createQuery(ql);
            applyQueryParameter(query);
            return query;
        }

        private TypedQuery<Map> createGroupQuery(List<String> selectFields, List<String> groupFields) {
            return this.createGroupQuery(selectFields, groupFields, -1);
        }

        private TypedQuery<Map> createGroupQuery(List<String> selectFields, List<String> groupFields, int limit) {
            List<String> tempSelectFields = new ArrayList<>();
            for (String selectField : selectFields) {
                if (!selectField.contains(" as ") && !selectField.contains(" AS ") && !selectField.contains(" ")) {
                    tempSelectFields.add(selectField + " AS " + selectField);
                } else {
                    tempSelectFields.add(selectField);
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("select new map(")
                    .append(StringUtils.join(tempSelectFields, ","))
                    .append(")")
                    //table
                    .append(QueryUtils.getQueryString(FIND_ALL_QUERY_STRING, entityInformation.getEntityName()))
                    //where
                    .append(applyCondition())
                    .append(" group by ")
                    .append(StringUtils.join(groupFields, ","));
            TypedQuery<Map> query = entityManager.createQuery(QueryUtils.applySorting(sb.toString(), sort, ALIAS), Map.class);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            applyQueryParameter(query);
            return query;
        }

        private String applyCondition() {

            return isEmpty(condition) ? "" : " where " + condition;
        }

        private void applyQueryParameter(Query query) {
            if (params != null) {
                params.forEach((k, v) -> {
                    if (v != null) {
                        query.setParameter(k.toString(), v);
                    }
                });
            }

        }

    }

}
