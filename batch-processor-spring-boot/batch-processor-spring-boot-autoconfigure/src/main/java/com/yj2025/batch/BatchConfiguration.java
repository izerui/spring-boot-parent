package com.yj2025.batch;

import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * see: line:74 in {@link org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration}
 */
@Configuration
public class BatchConfiguration extends DefaultBatchConfiguration {

    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }

    @Override
    protected String getDatabaseType() throws MetaDataAccessException {
        return "mysql";
    }

    @Bean
    @Override
    public JobRepository jobRepository() throws BatchConfigurationException {
        return super.jobRepository();
    }

    /**
     * jobLauncher定义
     */
    @Bean
    @Override
    public JobLauncher jobLauncher() throws BatchConfigurationException {
        return super.jobLauncher();
    }

    @Bean
    @Override
    public JobOperator jobOperator() throws BatchConfigurationException {
        return super.jobOperator();
    }

    @Bean
    public BatchDataSourceScriptDatabaseInitializer batchDataSourceScriptDatabaseInitializer(DataSource dataSource) throws MetaDataAccessException {
        BatchProperties.Jdbc jdbc = new BatchProperties.Jdbc();
        jdbc.setPlatform(getDatabaseType());
        jdbc.setTablePrefix(getTablePrefix());
        jdbc.setInitializeSchema(DatabaseInitializationMode.ALWAYS);
        return new BatchDataSourceScriptDatabaseInitializer(dataSource, jdbc);
    }

}
