package com.yj2025.jdbc.dialect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jdbc.core.dialect.JdbcDb2Dialect;
import org.springframework.data.jdbc.core.dialect.JdbcMySqlDialect;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.data.jdbc.core.dialect.JdbcSqlServerDialect;
import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.relational.core.dialect.*;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

public class CustormDialectProvider implements DialectResolver.JdbcDialectProvider {
    private static final Log LOG = LogFactory.getLog(DialectResolver.class);

    @Override
    public Optional<Dialect> getDialect(JdbcOperations operations) {
        return Optional.ofNullable(operations.execute((ConnectionCallback<Dialect>) CustormDialectProvider::getDialect));
    }

    @Nullable
    private static Dialect getDialect(Connection connection) throws SQLException {

        DatabaseMetaData metaData = connection.getMetaData();

        String name = metaData.getDatabaseProductName().toLowerCase(Locale.ENGLISH);

        if (name.contains("hsql")) {
            return HsqlDbDialect.INSTANCE;
        }
        if (name.contains("h2")) {
            return H2Dialect.INSTANCE;
        }
        if (name.contains("mysql")) {
            return new CustormJdbcMySqlDialect(getIdentifierProcessing(metaData));
        }
        if (name.contains("mariadb")) {
            return new MariaDbDialect(getIdentifierProcessing(metaData));
        }
        if (name.contains("postgresql")) {
            return JdbcPostgresDialect.INSTANCE;
        }
        if (name.contains("microsoft")) {
            return JdbcSqlServerDialect.INSTANCE;
        }
        if (name.contains("db2")) {
            return JdbcDb2Dialect.INSTANCE;
        }
        if (name.contains("oracle")) {
            return OracleDialect.INSTANCE;
        }

        LOG.info(String.format("Couldn't determine Dialect for \"%s\"", name));
        return null;
    }

    private static IdentifierProcessing getIdentifierProcessing(DatabaseMetaData metaData) throws SQLException {

        // getIdentifierQuoteString() returns a space " " if identifier quoting is not
        // supported.
        String quoteString = metaData.getIdentifierQuoteString();
        IdentifierProcessing.Quoting quoting = StringUtils.hasText(quoteString)
                ? new IdentifierProcessing.Quoting(quoteString)
                : IdentifierProcessing.Quoting.NONE;

        IdentifierProcessing.LetterCasing letterCasing;
        // IdentifierProcessing tries to mimic the behavior of unquoted identifiers for their quoted variants.
        if (metaData.supportsMixedCaseIdentifiers()) {
            letterCasing = IdentifierProcessing.LetterCasing.AS_IS;
        } else if (metaData.storesUpperCaseIdentifiers()) {
            letterCasing = IdentifierProcessing.LetterCasing.UPPER_CASE;
        } else if (metaData.storesLowerCaseIdentifiers()) {
            letterCasing = IdentifierProcessing.LetterCasing.LOWER_CASE;
        } else { // this shouldn't happen since one of the previous cases should be true.
            // But if it does happen, we go with the ANSI default.
            letterCasing = IdentifierProcessing.LetterCasing.UPPER_CASE;
        }

        return IdentifierProcessing.create(quoting, letterCasing);
    }
}
