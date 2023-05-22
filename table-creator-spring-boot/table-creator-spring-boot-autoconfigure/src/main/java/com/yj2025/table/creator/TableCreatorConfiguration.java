package com.yj2025.table.creator;

import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.internal.JdbcServicesImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.schema.extract.internal.DatabaseInformationImpl;
import org.hibernate.tool.schema.extract.spi.InformationExtractor;
import org.hibernate.tool.schema.internal.ExceptionHandlerHaltImpl;
import org.hibernate.tool.schema.internal.Helper;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Configuration
@AutoConfigureAfter(JpaRepositoriesAutoConfiguration.class)
public class TableCreatorConfiguration {

    @Primary
    @Bean
    public TableCreatorTemplate tableCreatorTemplate(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) entityManagerFactoryBean.getNativeEntityManagerFactory();

        ServiceRegistryImplementor serviceRegistry = sessionFactory.getServiceRegistry();
        HibernateSchemaManagementTool tool = (HibernateSchemaManagementTool) serviceRegistry.getService(SchemaManagementTool.class);
        JdbcServicesImpl jdbcServices = (JdbcServicesImpl) sessionFactory.getJdbcServices();

        Map config = new HashMap(serviceRegistry.getService(ConfigurationService.class).getSettings());
        ExecutionOptions options = SchemaManagementToolCoordinator.buildExecutionOptions(
                config,
                ExceptionHandlerHaltImpl.INSTANCE
        );

        final JdbcContext jdbcContext = tool.resolveJdbcContext(options.getConfigurationValues());
        final DdlTransactionIsolator ddlTransactionIsolator = tool.getDdlTransactionIsolator(jdbcContext);

        DatabaseInformationImpl databaseInformation = (DatabaseInformationImpl) Helper.buildDatabaseInformation(
                tool.getServiceRegistry(),
                ddlTransactionIsolator,
                sessionFactory.getSqlStringGenerationContext(),
                tool
        );
        Field field = ReflectionUtils.findField(DatabaseInformationImpl.class, "extractor");
        field.setAccessible(true);
        InformationExtractor extractor = (InformationExtractor) ReflectionUtils.getField(field, databaseInformation);
        return new TableCreatorTemplate(extractor, jdbcServices);
    }
}
