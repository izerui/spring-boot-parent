package com.yj2025.jdbc.dialect;

import com.yj2025.jdbc.dialect.flag.QueryFlagThreadLocalHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jdbc.core.dialect.JdbcMySqlDialect;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.data.relational.core.sql.Select;

import java.util.function.Function;

public class CustormJdbcMySqlDialect extends JdbcMySqlDialect {
    public CustormJdbcMySqlDialect(IdentifierProcessing identifierProcessing) {
        super(identifierProcessing);
    }

    @Override
    protected Function<Select, CharSequence> getAfterFromTable() {
        return super.getAfterFromTable().andThen(charSequence -> {
            String queryFlag = QueryFlagThreadLocalHolder.getQueryFlag();
            if (StringUtils.isNotBlank(queryFlag)) {
                return " /* " + queryFlag + " */";
            }
            return "";
        });
    }

}
