package com.yj2025.mybatis.interceptor;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.yj2025.mybatis.override.OverrideMybatisMapperMethod;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * spring data 分页拦截器
 */
public class DataPaginationInnerInterceptor extends PaginationInnerInterceptor {


    /**
     * spring data commons 分页
     */
    @Override
    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        Pageable page = findPage(parameter).orElse(null);
        if (page == null || page.getPageSize() < 0 || page.isUnpaged()) {
            return true;
        }

        BoundSql countSql;
        MappedStatement countMs = buildCountMappedStatement(ms, null);
        if (countMs != null) {
            countSql = countMs.getBoundSql(parameter);
        } else {
            countMs = buildAutoCountMappedStatement(ms);
            String countSqlStr = autoCountSql(new Page<>(page.getPageNumber(),page.getPageSize()), boundSql.getSql());
            PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
            countSql = new BoundSql(countMs.getConfiguration(), countSqlStr, mpBoundSql.parameterMappings(), parameter);
            PluginUtils.setAdditionalParameter(countSql, mpBoundSql.additionalParameters());
        }

        CacheKey cacheKey = executor.createCacheKey(countMs, parameter, rowBounds, countSql);
        List<Object> result = executor.query(countMs, parameter, rowBounds, resultHandler, cacheKey, countSql);
        long total = 0;
        if (CollectionUtils.isNotEmpty(result)) {
            // 个别数据库 count 没数据不会返回 0
            Object o = result.get(0);
            if (o != null) {
                total = Long.parseLong(o.toString());
            }
        }
        OverrideMybatisMapperMethod.setCountToLocalThread(total);
        return continuePage(page);
    }


    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        Pageable page = findPage(parameter).orElse(null);
        if (null == page) {
            return;
        }

        // 处理 orderBy 拼接
        boolean addOrdered = false;
        String buildSql = boundSql.getSql();
        Sort sort = page.getSort();
        if (sort != null) {
            addOrdered = true;
            buildSql = this.concatOrderBy(buildSql, sort);
        }

        // size 小于 0 且不限制返回值则不构造分页sql
        if (page.getPageSize() < 0) {
            if (addOrdered) {
                PluginUtils.mpBoundSql(boundSql).sql(buildSql);
            }
            return;
        }

        IDialect dialect = findIDialect(executor);

        final Configuration configuration = ms.getConfiguration();
        DialectModel model = dialect.buildPaginationSql(buildSql, page.getOffset(), page.getPageSize());
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);

        List<ParameterMapping> mappings = mpBoundSql.parameterMappings();
        Map<String, Object> additionalParameter = mpBoundSql.additionalParameters();
        model.consumers(mappings, configuration, additionalParameter);
        mpBoundSql.sql(model.getDialectSql());
        mpBoundSql.parameterMappings(mappings);
    }


    /**
     * 查找分页参数
     *
     * @param parameterObject 参数对象
     * @return 分页参数
     */
    private static Optional<Pageable> findPage(Object parameterObject) {
        if (parameterObject != null) {
            if (parameterObject instanceof Map) {
                Map<?, ?> parameterMap = (Map<?, ?>) parameterObject;
                for (Map.Entry entry : parameterMap.entrySet()) {
                    if (entry.getValue() != null && entry.getValue() instanceof Pageable) {
                        return Optional.of((Pageable) entry.getValue());
                    }
                }
            } else if (parameterObject instanceof Pageable) {
                return Optional.of((Pageable) parameterObject);
            }
        }
        return Optional.empty();
    }


    /**
     * count 查询之后,是否继续执行分页
     */
    protected boolean continuePage(Pageable pageable) {
        if (OverrideMybatisMapperMethod.getCountFromLocalThread() <= 0) {
            return false;
        }
        if (pageable.getPageNumber() + 1 > getTotalPages(pageable.getPageSize(), OverrideMybatisMapperMethod.getCountFromLocalThread())) {
            logger.warn("com.yj2025.mybatis.inner.DataPaginationInnerInterceptor#continuePage(): 超过最大范围，逻辑中断 list 执行");
            // 超过最大范围，未设置溢出逻辑中断 list 执行
            return false;
        }
        return true;
    }

    private int getTotalPages(int pageSize, long total) {
        return pageSize == 0 ? 1 : (int) Math.ceil((double) total / (double) pageSize);
    }

    /**
     * 查询SQL拼接Order By
     *
     * @param originalSql 需要拼接的SQL
     * @return ignore
     */
    public String concatOrderBy(String originalSql, Sort sort) {
        try {
            Select select = (Select) CCJSqlParserUtil.parse(originalSql);
            SelectBody selectBody = select.getSelectBody();
            if (selectBody instanceof PlainSelect) {
                PlainSelect plainSelect = (PlainSelect) selectBody;
                List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
                List<OrderByElement> orderByElementsReturn = addOrderByElements(sort, orderByElements);
                plainSelect.setOrderByElements(orderByElementsReturn);
                return select.toString();
            } else if (selectBody instanceof SetOperationList) {
                SetOperationList setOperationList = (SetOperationList) selectBody;
                List<OrderByElement> orderByElements = setOperationList.getOrderByElements();
                List<OrderByElement> orderByElementsReturn = addOrderByElements(sort, orderByElements);
                setOperationList.setOrderByElements(orderByElementsReturn);
                return select.toString();
            } else if (selectBody instanceof WithItem) {
                // todo: don't known how to resole
                return originalSql;
            } else {
                return originalSql;
            }
        } catch (JSQLParserException e) {
            logger.warn("failed to concat orderBy from IPage, exception:\n" + e.getCause());
        } catch (Exception e) {
            logger.warn("failed to concat orderBy from IPage, exception:\n" + e);
        }
        return originalSql;
    }

    protected List<OrderByElement> addOrderByElements(Sort sort, List<OrderByElement> orderByElements) {
        List<OrderByElement> additionalOrderBy = sort.stream()
                .filter(item -> StringUtils.isNotBlank(item.getProperty()))
                .map(item -> {
                    OrderByElement element = new OrderByElement();
                    element.setExpression(new Column(item.getProperty()));
                    element.setAsc(item.getDirection().isAscending());
                    element.setAscDescPresent(true);
                    return element;
                }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(orderByElements)) {
            return additionalOrderBy;
        }
        // github pull/3550 优化排序，比如：默认 order by id 前端传了name排序，设置为 order by name,id
        additionalOrderBy.addAll(orderByElements);
        return additionalOrderBy;
    }

}
