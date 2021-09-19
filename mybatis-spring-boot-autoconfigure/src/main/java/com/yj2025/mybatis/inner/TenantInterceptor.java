package com.yj2025.mybatis.inner;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TenantInterceptor extends JsqlParserSupport implements InnerInterceptor {

    private String tenantField;

    private static final Map<String, Boolean> INTERCEPTOR_IGNORE_CACHE = new ConcurrentHashMap<>();

    public TenantInterceptor(String tenantField) {
        Assert.notNull(tenantField, "既然设置了租户校验,字段怎么能为空呢?");
        this.tenantField = tenantField;
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler handler = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = handler.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE || sct == SqlCommandType.SELECT || sct == SqlCommandType.INSERT) {
            // 每个mapper方法调用只验证一次
            if (this.willIgnore(ms.getId())) {
                return;
            }
            BoundSql boundSql = handler.boundSql();
            // 验证是否存在租户字段
            parserMulti(boundSql.getSql(), null);
            // 验证通过后,设置当前id为true,后续不再校验,提升性能
            INTERCEPTOR_IGNORE_CACHE.put(ms.getId(), true);
        }
    }

    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {
        this.checkTenantId(delete.getWhere());
    }

    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        this.checkTenantId(update.getWhere());
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        this.checkTenantId(((PlainSelect) select.getSelectBody()).getWhere());
    }

    @Override
    protected void processInsert(Insert insert, int index, String sql, Object obj) {
        Assert.notEmpty(insert.getColumns(), "insert 语句怎么能少要插入的字段呢?");
        Optional<String> optional = insert.getColumns().stream().map(Column::getColumnName).filter(s -> tenantField.equals(s)).findAny();
        Assert.isTrue(optional.isPresent(), "非法SQL，必须要有租户字段: [" + tenantField + "]");
    }

    private boolean willIgnore(String id) {
        Boolean ignore = INTERCEPTOR_IGNORE_CACHE.get(id);
        if (ignore == null) {
            return false;
        }
        return ignore;
    }

    /**
     * 校验必须有租户ID
     *
     * @param where
     */
    private void checkTenantId(Expression where) {
        AtomicBoolean tenantFieldExist = new AtomicBoolean(false);
        if (where != null) {
            where.accept(new ExpressionVisitorAdapter() {
                @Override
                public void visit(EqualsTo expr) {
                    Expression leftExpression = expr.getLeftExpression();
                    if (leftExpression == null || tenantFieldExist.get()) {
                        return;
                    }
                    if (leftExpression instanceof Column) {
                        String columnName = ((Column) leftExpression).getColumnName();
                        tenantFieldExist.set(tenantField.equals(columnName));
                    }
                }
            });
        }
        Assert.isTrue(tenantFieldExist.get(), "非法SQL，必须要有租户字段: [" + tenantField + "]");
    }

}
