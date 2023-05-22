package com.yj2025.table.creator;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.tool.schema.extract.internal.TableInformationImpl;
import org.hibernate.tool.schema.extract.spi.*;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class TableCreatorTemplate {

    private InformationExtractor informationExtractor;
    private JdbcServices jdbcServices;

    public TableCreatorTemplate(InformationExtractor informationExtractor, JdbcServices jdbcServices) {
        this.informationExtractor = informationExtractor;
        this.jdbcServices = jdbcServices;
    }

    public TableInformation getTable(String tableName) {
        return informationExtractor.getTable(jdbcServices.getJdbcEnvironment().getCurrentCatalog(), jdbcServices.getJdbcEnvironment().getCurrentSchema(), Identifier.toIdentifier(tableName, true));
    }

    public Map<String, TableInformation> getTables() {
        NameSpaceTablesInformation tables = informationExtractor.getTables(jdbcServices.getJdbcEnvironment().getCurrentCatalog(), jdbcServices.getJdbcEnvironment().getCurrentSchema());
        Field field = ReflectionUtils.findField(NameSpaceTablesInformation.class, "tables");
        field.setAccessible(true);
        return (Map<String, TableInformation>) ReflectionUtils.getField(field, tables);
    }

    public PrimaryKeyInformation getPrimaryKey(String tableName) {
        TableInformation tableInformation = this.getTable(tableName);
        return informationExtractor.getPrimaryKey((TableInformationImpl) tableInformation);
    }

    public Iterable<IndexInformation> getIndexes(String tableName) {
        TableInformation tableInformation = this.getTable(tableName);
        return informationExtractor.getIndexes(tableInformation);
    }

    public Iterable<ForeignKeyInformation> getForeignKeys(String tableName) {
        TableInformation tableInformation = this.getTable(tableName);
        return informationExtractor.getForeignKeys(tableInformation);
    }


}
