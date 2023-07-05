package com.yj2025.jdbc;

import com.yj2025.jdbc.override.OverrideDefaultNamingStrategy;
import com.yj2025.jdbc.override.OverrideJdbcMappingContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.core.convert.RelationResolver;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.RelationalManagedTypes;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.DefaultNamingStrategy;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;

/**
 * 覆盖自动装配: {@link org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration.SpringBootJdbcConfiguration}
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class JdbcConfiguration extends AbstractJdbcConfiguration {

    private final ApplicationContext applicationContext;

    public JdbcConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

//    @Bean
//    public RepositoryQueryAspect repositoryQueryAspect() {
//        return new RepositoryQueryAspect();
//    }

    @Bean
    @Primary
    @ConditionalOnMissingClass
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }


    @Bean
    public NamingStrategy namingStrategy() {
        return OverrideDefaultNamingStrategy.INSTANCE;
    }


    // ---------------------------------------------------------------------------------------------------

    @Override
    protected Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {
        return new EntityScanner(this.applicationContext).scan(Table.class);
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public RelationalManagedTypes jdbcManagedTypes() throws ClassNotFoundException {
        return super.jdbcManagedTypes();
    }

    /**
     * 覆盖: {@link JdbcMappingContext}
     *
     * @param namingStrategy    optional {@link NamingStrategy}. Use {@link org.springframework.data.relational.core.mapping.DefaultNamingStrategy#INSTANCE} as fallback.
     * @param customConversions see {@link #jdbcCustomConversions()}.
     * @param jdbcManagedTypes  JDBC managed types, typically discovered through {@link #jdbcManagedTypes() an entity
     *                          scan}.
     * @return
     */
    @Override
    @Bean
    public JdbcMappingContext jdbcMappingContext(Optional<NamingStrategy> namingStrategy,
                                                 JdbcCustomConversions customConversions, RelationalManagedTypes jdbcManagedTypes) {
        OverrideJdbcMappingContext overrideJdbcMappingContext = new OverrideJdbcMappingContext(namingStrategy.orElse(DefaultNamingStrategy.INSTANCE));
        overrideJdbcMappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        overrideJdbcMappingContext.setManagedTypes(jdbcManagedTypes);

        return overrideJdbcMappingContext;
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public JdbcConverter jdbcConverter(JdbcMappingContext mappingContext, NamedParameterJdbcOperations operations,
                                       @Lazy RelationResolver relationResolver, JdbcCustomConversions conversions, Dialect dialect) {
        return super.jdbcConverter(mappingContext, operations, relationResolver, conversions, dialect);
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public JdbcCustomConversions jdbcCustomConversions() {
        return super.jdbcCustomConversions();
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public JdbcAggregateTemplate jdbcAggregateTemplate(ApplicationContext applicationContext,
                                                       JdbcMappingContext mappingContext, JdbcConverter converter, DataAccessStrategy dataAccessStrategy) {
        return super.jdbcAggregateTemplate(applicationContext, mappingContext, converter, dataAccessStrategy);
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public DataAccessStrategy dataAccessStrategyBean(NamedParameterJdbcOperations operations,
                                                     JdbcConverter jdbcConverter, JdbcMappingContext context, Dialect dialect) {
        return super.dataAccessStrategyBean(operations, jdbcConverter, context, dialect);
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public Dialect jdbcDialect(NamedParameterJdbcOperations operations) {
        return super.jdbcDialect(operations);
    }
}
