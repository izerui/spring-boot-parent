package com.yj2025.jdbc.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.QueryMapper;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.RenderContextFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.sql.*;
import org.springframework.data.relational.core.sql.render.RenderContext;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomSqlGenerator {

    private final RelationalMappingContext mappingContext;
    private final JdbcConverter converter;
    private final RelationalPersistentEntity<?> entity;
    private final Dialect dialect;
    private final RenderContext renderContext;
    private final SqlRenderer sqlRenderer;
    private final QueryMapper queryMapper;

    public CustomSqlGenerator(RelationalMappingContext mappingContext, JdbcConverter converter, RelationalPersistentEntity<?> entity,
                              Dialect dialect) {
        this.mappingContext = mappingContext;
        this.converter = converter;
        this.entity = entity;
        this.renderContext = new RenderContextFactory(dialect).createRenderContext();
        this.sqlRenderer = SqlRenderer.create(renderContext);
        this.queryMapper = new QueryMapper(dialect, converter);
        this.dialect = dialect;
    }

    public Table getTable() {
        return Table.create(entity.getQualifiedTableName());
    }

    public String getSelectWhereSql(Collection<String> selectColumns, Query query, MapSqlParameterSource parameterSource) {
        Assert.notNull(selectColumns, "查询的字段不能为空!");
        Table table = getTable();
        SelectBuilder.SelectWhere selectWhere = StatementBuilder
                .select(selectColumns.stream().map(s -> new OriginalSqlIdentifier(s)).collect(Collectors.toList()))
                .from(table);
        SelectBuilder.SelectOrdered selectOrdered = query //
                .getCriteria() //
                .map(item -> this.applyCriteria(item, selectWhere, parameterSource, table)) //
                .orElse(selectWhere);
        if (query.isSorted()) {
            List<OrderByField> sort = this.queryMapper.getMappedSort(table, query.getSort(), entity);
            selectOrdered = selectWhere.orderBy(sort);
        }
        Select select = selectOrdered.build();
        String sql = sqlRenderer.render(select);
        return sql;
    }

    public String getGroupSql(Collection<String> selectColumns, Collection<String> groupColumns, Query query, MapSqlParameterSource parameterSource) {
        String sql = getSelectWhereSql(selectColumns, query, parameterSource);
        if (groupColumns != null && !groupColumns.isEmpty()) {
            sql += " group by " + StringUtils.join(groupColumns, ",");
        }
        return sql;
    }

    /**
     * 添加条件
     */
    private SelectBuilder.SelectOrdered applyCriteria(@Nullable CriteriaDefinition criteria,
                                                      SelectBuilder.SelectWhere selectWhere, MapSqlParameterSource parameterSource, Table table) {
        return criteria == null || criteria.isEmpty() // Check for null and empty criteria
                ? selectWhere //
                : selectWhere.where(queryMapper.getMappedObject(parameterSource, criteria, table, entity));
    }


}
