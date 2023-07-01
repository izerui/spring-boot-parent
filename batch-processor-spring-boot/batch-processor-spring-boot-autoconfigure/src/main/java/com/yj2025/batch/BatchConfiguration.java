package com.yj2025.batch;

import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration extends DefaultBatchConfiguration {

    /**
     * JobRepository定义：设置数据库，注册Job容器
     */
    @Bean
    public JobRepositoryFactoryBean jobRepositoryFactoryBean(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDatabaseType("mysql");
        jobRepositoryFactoryBean.setTablePrefix("SB_");
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.afterPropertiesSet();
        return jobRepositoryFactoryBean;
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
    public SimpleJobOperator jobOperator(JobExplorer jobExplorer,
                                         JobRepository jobRepository,
                                         JobLauncher jobLauncher,
                                         JobRegistry jobRegistry) {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobLauncher(jobLauncher);
        return jobOperator;
    }

}
