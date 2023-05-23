package com.yj2025.creator001;

import com.yj2025.table.creator.TableCreatorTemplate;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.sql.Types;
import java.util.Iterator;
import java.util.Map;

@SpringBootApplication
@EnableJpaRepositories
public class TableCreatorApplication implements CommandLineRunner {

    @Autowired
    private TableCreatorTemplate tableCreatorTemplate;

    public static void main(String[] args) {
        SpringApplication.run(TableCreatorApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, TableInformation> tables = tableCreatorTemplate.getTables();
        tables.forEach((s, tableInformation) -> {
            System.out.println(s);
        });

        testCreate();
        testAlert();
    }

    private void testAlert() {
        Table table = new Table("test_user");

        Column nameColumn = new Column("name222222");
        nameColumn.setLength(500);
        nameColumn.setSqlType(tableCreatorTemplate.getDialect().getTypeName(Types.NVARCHAR, 500L, 0, 0));
        nameColumn.setComment("名称222222");

        Column nameColumn1 = new Column("code");
        nameColumn1.setLength(46);
        nameColumn1.setSqlType(tableCreatorTemplate.getDialect().getTypeName(Types.VARCHAR, 34L, 0, 0));
        nameColumn1.setComment("变更code");


        table.addColumn(nameColumn);
        table.addColumn(nameColumn1);
        tableCreatorTemplate.alertTable(table);
    }

    private void testCreate() {
        Table table = new Table("TEST01");

        Column idColumn = new Column("id");
        idColumn.setSqlType(tableCreatorTemplate.getDialect().getTypeName(Types.INTEGER, 100L, 0, 0));
        idColumn.setNullable(false);
        idColumn.setUnique(true);
        idColumn.setComment("id主键");

        Column nameColumn = new Column("name");
        nameColumn.setLength(500);
        nameColumn.setSqlType(tableCreatorTemplate.getDialect().getTypeName(Types.NVARCHAR, 500L, 0, 0));
        nameColumn.setComment("名称");

        PrimaryKey primaryKey = new PrimaryKey(table);
        primaryKey.addColumn(idColumn);
        primaryKey.setName("id");

        table.addColumn(idColumn);
        table.addColumn(nameColumn);
        table.setPrimaryKey(primaryKey);
        tableCreatorTemplate.createTable(table);
    }
}
