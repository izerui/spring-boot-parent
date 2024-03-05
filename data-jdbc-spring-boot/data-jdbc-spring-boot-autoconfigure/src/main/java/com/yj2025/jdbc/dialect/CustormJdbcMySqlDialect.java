package com.yj2025.jdbc.dialect;

import com.yj2025.jdbc.converter.BooleanToIntegerConverter;
import com.yj2025.jdbc.converter.IntegerToBooleanConverter;
import com.yj2025.jdbc.dialect.flag.QueryFlag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jdbc.core.dialect.JdbcMySqlDialect;
import org.springframework.data.relational.core.dialect.LockClause;
import org.springframework.data.relational.core.sql.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.yj2025.jdbc.dialect.flag.QueryFlagThreadLocalHolder.getQueryFlags;

public class CustormJdbcMySqlDialect extends JdbcMySqlDialect {
    public CustormJdbcMySqlDialect(IdentifierProcessing identifierProcessing) {
        super(identifierProcessing);
    }

    @Override
    protected Function<Select, CharSequence> getAfterFromTable() {
        Function<Select, ? extends CharSequence> afterFromTable = select -> {
            List<TableLike> tables = select.getFrom().getTables();
            if (tables.isEmpty()) {
                return "";
            }
            // get the first table and obtain last part if the identifier is a composed one.
            SqlIdentifier identifier = tables.get(0).getName();
            SqlIdentifier last = identifier;

            for (SqlIdentifier sqlIdentifier : identifier) {
                last = sqlIdentifier;
            }

            // without schema
            String tableName = last.toSql(IdentifierProcessing.NONE);

            List<QueryFlag> queryFlags = getQueryFlags();
            if (queryFlags != null && !queryFlags.isEmpty()) {
                for (QueryFlag queryFlag : queryFlags) {
                    if ("".equals(queryFlag.getTablePrefix()) || (tableName != null && tableName.startsWith(queryFlag.getTablePrefix()))) {
                        String value = queryFlag.getValue();
                        if (StringUtils.isNotEmpty(value)) {
                            return "\n".concat(queryFlag.isComment() ? "/* " + value + " */" : value);
                        }
                    }
                }
            }
            return "\n";
        };

        // 如果是其他类型的锁表表后置语句...
        LockClause lockClause = lock();
        if (lockClause.getClausePosition() == LockClause.Position.AFTER_FROM_TABLE) {
            afterFromTable = new LockRenderFunction(lockClause);
        }

        return afterFromTable.andThen(PrependWithLeadingWhitespace.INSTANCE);
    }

    @Override
    public Collection<Object> getConverters() {
        Collection<Object> converters = super.getConverters();
        converters.addAll(Arrays.asList(
//                new BooleanToStringConverter(),
//                new StringToBooleanConverter(),
                new BooleanToIntegerConverter(),
                new IntegerToBooleanConverter()
        ));
        return converters;
    }


    /**
     * {@code LOCK} function rendering the {@link LockClause}.
     */
    static class LockRenderFunction implements Function<Select, CharSequence> {

        private final LockClause clause;

        public LockRenderFunction(LockClause clause) {
            this.clause = clause;
        }

        @Override
        public CharSequence apply(Select select) {

            LockMode lockMode = select.getLockMode();

            if (lockMode == null) {
                return "";
            }

            return clause.getLock(new LockOptions(lockMode, select.getFrom()));
        }
    }

    /**
     * Prepends a non-empty rendering result with a leading whitespace,
     */
    enum PrependWithLeadingWhitespace implements Function<CharSequence, CharSequence> {

        INSTANCE;

        @Override
        public CharSequence apply(CharSequence charSequence) {

            if (charSequence.length() == 0) {
                return charSequence;
            }

            return " " + charSequence;
        }
    }
}
