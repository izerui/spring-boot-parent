package com.yj2025.jdbc;

import com.yj2025.jdbc.override.OverrideDefaultNamingStrategy;
import com.yj2025.jdbc.tenant.TenantMethodAspect;
import com.yj2025.jdbc.tenant.TenantSharding;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
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

    @Bean
    public TenantMethodAspect repositoryQueryAspect() {
        return new TenantMethodAspect(applicationContext);
    }

    @Bean
    public TenantSharding tenantSharding() {
        return new TenantSharding(applicationContext);
    }

    @Bean
    @Primary
    @ConditionalOnMissingClass
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    // 覆盖没有声明@Table的情况下的表名生成策略
    @Bean
    public NamingStrategy namingStrategy() {
        return new OverrideDefaultNamingStrategy();
    }


    // ---------------------------------------------------------------------------------------------------

    @Override
    protected Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {
        return new EntityScanner(this.applicationContext).scan(Table.class);
    }

}
