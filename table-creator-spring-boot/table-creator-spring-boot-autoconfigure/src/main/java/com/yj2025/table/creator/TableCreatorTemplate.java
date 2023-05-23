package com.yj2025.table.creator;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.internal.DatabaseInformationImpl;
import org.hibernate.tool.schema.extract.internal.TableInformationImpl;
import org.hibernate.tool.schema.extract.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

public class TableCreatorTemplate {

    private final static Logger LOGGER = LoggerFactory.getLogger(TableCreatorTemplate.class);

    private final DatabaseInformationImpl databaseInformation;
    private final JdbcServices jdbcServices;
    private final InformationExtractor extractor;
    private final SqlStringGenerationContext sqlStringGenerationContext;
    private final Identifier currentCatalog;
    private final Identifier currentSchema;

    public TableCreatorTemplate(DatabaseInformationImpl databaseInformation, JdbcServices jdbcServices) {
        this.databaseInformation = databaseInformation;
        this.jdbcServices = jdbcServices;
        Field field = ReflectionUtils.findField(DatabaseInformationImpl.class, "extractor");
        field.setAccessible(true);
        this.extractor = (InformationExtractor) ReflectionUtils.getField(field, databaseInformation);

        Field field1 = ReflectionUtils.findField(DatabaseInformationImpl.class, "sqlStringGenerationContext");
        field1.setAccessible(true);
        this.sqlStringGenerationContext = (SqlStringGenerationContext) ReflectionUtils.getField(field1, databaseInformation);
        this.currentCatalog = jdbcServices.getJdbcEnvironment().getCurrentCatalog();
        this.currentSchema = jdbcServices.getJdbcEnvironment().getCurrentSchema();
    }

    private String getCurrentString(Identifier identifier) {
        if (identifier == null) {
            return null;
        }
        return identifier.toString();
    }

    /**
     * 根据表名获取表信息
     *
     * @param tableName
     * @return
     */
    public TableInformation getTable(String tableName) {
        return extractor.getTable(currentCatalog, currentSchema, Identifier.toIdentifier(tableName, true));
    }

    /**
     * 获取所有表信息集合
     *
     * @return
     */
    public Map<String, TableInformation> getTables() {
        NameSpaceTablesInformation tables = extractor.getTables(currentCatalog, currentSchema);
        Field field = ReflectionUtils.findField(NameSpaceTablesInformation.class, "tables");
        field.setAccessible(true);
        return (Map<String, TableInformation>) ReflectionUtils.getField(field, tables);
    }

    /**
     * 获取指定表的主键
     *
     * @param tableName
     * @return
     */
    public PrimaryKeyInformation getPrimaryKey(String tableName) {
        TableInformation tableInformation = this.getTable(tableName);
        return extractor.getPrimaryKey((TableInformationImpl) tableInformation);
    }

    /**
     * 获取指定表的索引
     *
     * @param tableName
     * @return
     */
    public Iterable<IndexInformation> getIndexes(String tableName) {
        TableInformation tableInformation = this.getTable(tableName);
        return extractor.getIndexes(tableInformation);
    }

    /**
     * 获取指定表的外键
     *
     * @param tableName
     * @return
     */
    public Iterable<ForeignKeyInformation> getForeignKeys(String tableName) {
        TableInformation tableInformation = this.getTable(tableName);
        return extractor.getForeignKeys(tableInformation);
    }

    /**
     * 获取方言
     *
     * @return
     */
    public Dialect getDialect() {
        return this.sqlStringGenerationContext.getDialect();
    }

    private void executeSQL(String sql) {
        JdbcConnectionAccess jdbcConnectionAccess = jdbcServices.getBootstrapJdbcConnectionAccess();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = jdbcConnectionAccess.obtainConnection();
            statement = connection.createStatement();
            statement.execute(sql);
            statement.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (connection != null) {
                try {
                    jdbcConnectionAccess.releaseConnection(connection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 获取表的创建语句
     *
     * @param table
     * @return
     */
    public void createTable(Table table) {
        String createSQL = table.sqlCreateString(null, this.sqlStringGenerationContext, getCurrentString(currentCatalog), getCurrentString(currentSchema));
        LOGGER.info("create: {}", createSQL);
        executeSQL(createSQL);
    }

    /**
     * 获取表的创建语句
     *
     * @param table
     * @return
     */
    public void alertTable(Table table) {
        Iterator<String> sqlAlterStrings = table.sqlAlterStrings(getDialect(), null, getTable(table.getName()), sqlStringGenerationContext);
        sqlAlterStrings.forEachRemaining(s -> {
            LOGGER.info("alert: {}", s);
            executeSQL(s);
        });
    }


}
