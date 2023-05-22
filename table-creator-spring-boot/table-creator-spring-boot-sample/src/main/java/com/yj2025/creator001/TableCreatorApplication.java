package com.yj2025.creator001;

import com.yj2025.table.creator.TableCreatorConfiguration;
import com.yj2025.table.creator.TableCreatorTemplate;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
    }
}
