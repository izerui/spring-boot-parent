package com.yj2025.cost;

import com.yj2025.table.creator.TableTemplate;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

import java.sql.JDBCType;

public class FinishedInventoryTempTableCreator {

    private final String tableName;
    private final TableTemplate tableTemplate;

    public FinishedInventoryTempTableCreator(String tableName, TableTemplate tableTemplate) {
        this.tableName = tableName;
        this.tableTemplate = tableTemplate;
    }

    public void createTable() {
        if (!tableTemplate.existTable("test", null, tableName)) {
            Table table = new Table(tableName);
            Column bomIdColumn = new Column("bom_id");
            bomIdColumn.setSqlType(tableTemplate.columnType(JDBCType.VARCHAR, 128L));
            bomIdColumn.setNullable(false);
            bomIdColumn.setUnique(true);
            bomIdColumn.setComment("bom_id");

            Column invColumn = new Column("inventory_id");
            invColumn.setSqlType(tableTemplate.columnType(JDBCType.VARCHAR, 128L));
            invColumn.setComment("inventory_id");

            Column ymColumn = new Column("ym");
            ymColumn.setSqlType(tableTemplate.columnType(JDBCType.BIGINT, 4L));
            ymColumn.setComment("年月");

            Column attributeCodeColumn = new Column("attribute_code");
            attributeCodeColumn.setSqlType(tableTemplate.columnType(JDBCType.VARCHAR, 64L));
            attributeCodeColumn.setComment("货品属性");

            Column quantityColumn = new Column("quantity");
            quantityColumn.setSqlType(tableTemplate.columnType(JDBCType.DECIMAL, 0L, 24, 8));
            quantityColumn.setDefaultValue("0.0000");

            PrimaryKey primaryKey = new PrimaryKey(table);
            primaryKey.addColumn(bomIdColumn);
            primaryKey.setName("bom_id");

            table.addColumn(bomIdColumn);
            table.addColumn(invColumn);
            table.addColumn(ymColumn);
            table.addColumn(attributeCodeColumn);
            table.addColumn(quantityColumn);
            table.setPrimaryKey(primaryKey);
            tableTemplate.createTable(table);
        }
    }

    public String getInsertPlaceholderSQL() {
        return String.format("""
                insert into %s (bom_id, inventory_id, attribute_code, ym, quantity) values (?, ?, ?, ?, ?)
                """, tableName);
    }

}
