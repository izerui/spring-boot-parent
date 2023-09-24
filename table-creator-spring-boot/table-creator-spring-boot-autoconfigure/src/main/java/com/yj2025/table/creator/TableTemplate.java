package com.yj2025.table.creator;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.internal.DatabaseInformationImpl;
import org.hibernate.tool.schema.extract.internal.TableInformationImpl;
import org.hibernate.tool.schema.extract.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

public class TableTemplate {

    private final static Logger LOGGER = LoggerFactory.getLogger(TableTemplate.class);

    private final DatabaseInformationImpl databaseInformation;
    private final JdbcServices jdbcServices;
    private final InformationExtractor extractor;
    private final SqlStringGenerationContext sqlStringGenerationContext;

    public TableTemplate(DatabaseInformationImpl databaseInformation, JdbcServices jdbcServices) {
        this.databaseInformation = databaseInformation;
        this.jdbcServices = jdbcServices;
        this.extractor = getPropertyValue(DatabaseInformationImpl.class, databaseInformation, "extractor");
        this.sqlStringGenerationContext = getPropertyValue(DatabaseInformationImpl.class, databaseInformation, "sqlStringGenerationContext");
    }

    private <C, T> T getPropertyValue(Class<C> cClass, C target, String property) {
        Field field = ReflectionUtils.findField(cClass, property);
        field.setAccessible(true);
        return (T) ReflectionUtils.getField(field, target);
    }

    /**
     * 获取当前连接的数据库
     *
     * @return
     */
    public String getCurrentCatalog() {
        Identifier currentCatalog = jdbcServices.getJdbcEnvironment().getCurrentCatalog();
        if (currentCatalog == null) {
            return null;
        }
        return currentCatalog.toString();
    }

    /**
     * 获取当前连接的数据库下的模式名
     *
     * @return
     */
    public String getCurrentSchema() {
        Identifier currentSchema = jdbcServices.getJdbcEnvironment().getCurrentSchema();
        if (currentSchema == null) {
            return null;
        }
        return currentSchema.toString();
    }

    /**
     * 判断表是否存在
     *
     * @return
     */
    public boolean existTable(String catalog, String schema, String tableName) {
        return extractor.getTable(Identifier.toIdentifier(catalog, true),
                Identifier.toIdentifier(schema, true),
                Identifier.toIdentifier(tableName, true)) != null;
    }

    /**
     * 判断表是否存在
     *
     * @param tableName
     * @return
     */
    public boolean existTable(String tableName) {
        return existTable(getCurrentCatalog(), getCurrentSchema(), tableName);
    }

    /**
     * 根据表名获取表信息
     *
     * @return
     */
    public TableInformation getTable(String catalog, String schema, String tableName) {
        return extractor.getTable(Identifier.toIdentifier(catalog, true),
                Identifier.toIdentifier(schema, true),
                Identifier.toIdentifier(tableName, true));
    }

    /**
     * 根据表名获取表信息
     *
     * @param tableName
     * @return
     */
    public TableInformation getTable(String tableName) {
        return extractor.getTable(jdbcServices.getJdbcEnvironment().getCurrentCatalog(),
                jdbcServices.getJdbcEnvironment().getCurrentSchema(),
                Identifier.toIdentifier(tableName, true));
    }

    /**
     * 获取所有表信息集合
     *
     * @return
     */
    public Map<String, TableInformation> getTables(String catalog, String schema) {
        NameSpaceTablesInformation tables = extractor.getTables(Identifier.toIdentifier(catalog, true), Identifier.toIdentifier(schema, true));
        Field field = ReflectionUtils.findField(NameSpaceTablesInformation.class, "tables");
        field.setAccessible(true);
        return (Map<String, TableInformation>) ReflectionUtils.getField(field, tables);
    }

    /**
     * 获取所有表信息集合
     *
     * @return
     */
    public Map<String, TableInformation> getTables() {
        return this.getTables(getCurrentCatalog(), getCurrentSchema());
    }

    /**
     * 获取指定表的主键
     *
     * @return
     */
    public PrimaryKeyInformation getPrimaryKey(String catalog, String schema, String tableName) {
        TableInformation tableInformation = this.getTable(catalog, schema, tableName);
        return extractor.getPrimaryKey((TableInformationImpl) tableInformation);
    }

    /**
     * 获取指定表的主键
     *
     * @return
     */
    public PrimaryKeyInformation getPrimaryKey(String tableName) {
        return getPrimaryKey(getCurrentCatalog(), getCurrentSchema(), tableName);
    }

    /**
     * 获取指定表的索引
     *
     * @return
     */
    public Iterable<IndexInformation> getIndexes(String catalog, String schema, String tableName) {
        TableInformation tableInformation = this.getTable(catalog, schema, tableName);
        return extractor.getIndexes(tableInformation);
    }

    /**
     * 获取指定表的索引
     *
     * @return
     */
    public Iterable<IndexInformation> getIndexes(String tableName) {
        return this.getIndexes(getCurrentCatalog(), getCurrentSchema(), tableName);
    }

    /**
     * 获取指定表的外键
     *
     * @return
     */
    public Iterable<ForeignKeyInformation> getForeignKeys(String catalog, String schema, String tableName) {
        TableInformation tableInformation = this.getTable(catalog, schema, tableName);
        return extractor.getForeignKeys(tableInformation);
    }

    /**
     * 获取指定表的外键
     *
     * @return
     */
    public Iterable<ForeignKeyInformation> getForeignKeys(String tableName) {
        return getForeignKeys(getCurrentCatalog(), getCurrentSchema(), tableName);
    }

    /**
     * 获取方言
     *
     * @return
     */
    public Dialect getDialect() {
        return this.sqlStringGenerationContext.getDialect();
    }


    /**
     * 根据jdbc类型获取字段数据库类型
     *
     * @param type      类型
     * @param length    字段长度
     * @param precision 精度
     * @param scale     小数点
     * @return
     */
    public String columnType(JDBCType type, long length, int precision, int scale) {
        return getDialect().getTypeName(type.getVendorTypeNumber(), length, precision, scale);
    }

    /**
     * 根据jdbc类型获取字段数据库类型
     *
     * @param type
     * @return
     */
    public String columnType(JDBCType type, long length) {
        return getDialect().getTypeName(type.getVendorTypeNumber(), length, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE);
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
    public void createTable(String catalog, String schema, Table table) {
        String createSQL = table.sqlCreateString(null, this.sqlStringGenerationContext, catalog, schema);
        LOGGER.info("create: {}", createSQL);
        executeSQL(createSQL);
    }

    /**
     * 获取表的创建语句
     *
     * @param table
     * @return
     */
    public void createTable(Table table) {
        createTable(getCurrentCatalog(), getCurrentSchema(), table);
    }

    /**
     * 获取表的创建语句
     *
     * @param table
     * @return
     */
    public void alertTable(String catalog, String schema, Table table) {
        Iterator<String> sqlAlterStrings = table.sqlAlterStrings(getDialect(), null, getTable(catalog, schema, table.getName()), sqlStringGenerationContext);
        sqlAlterStrings.forEachRemaining(s -> {
            LOGGER.info("alert: {}", s);
            executeSQL(s);
        });
    }

    /**
     * 获取表的创建语句
     *
     * @param table
     * @return
     */
    public void alertTable(Table table) {
        alertTable(getCurrentCatalog(), getCurrentSchema(), table);
    }

    /**
     * 根据表信息获取其所有字段
     *
     * @param tableInformation
     * @return
     */
    public Map<Identifier, ColumnInformation> getColumns(TableInformation tableInformation) {
        return getPropertyValue(TableInformationImpl.class, (TableInformationImpl) tableInformation, "columns");
    }


}
