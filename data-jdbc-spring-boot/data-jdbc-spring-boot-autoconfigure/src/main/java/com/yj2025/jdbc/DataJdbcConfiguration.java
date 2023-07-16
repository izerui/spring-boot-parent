package com.yj2025.jdbc;

import com.yj2025.jdbc.converter.BooleanToIntegerConverter;
import com.yj2025.jdbc.converter.BooleanToStringConverter;
import com.yj2025.jdbc.converter.IntegerToBooleanConverter;
import com.yj2025.jdbc.converter.StringToBooleanConverter;
import com.yj2025.jdbc.impl.PlatformJdbcRepositoryImpl;
import com.yj2025.jdbc.override.OverrideDefaultNamingStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * @see org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJdbcRepositories(repositoryBaseClass = PlatformJdbcRepositoryImpl.class)
public class DataJdbcConfiguration extends AbstractJdbcConfiguration {

    private final ApplicationContext applicationContext;

    public DataJdbcConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingClass
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }


    // 覆盖没有声明@Table的情况下的表名生成策略
    @Bean
    public NamingStrategy namingStrategy() {
        return new OverrideDefaultNamingStrategy();
    }


    @Override
    protected List<?> userConverters() {
        return Arrays.asList(
                new BooleanToStringConverter(),
                new StringToBooleanConverter(),
                new BooleanToIntegerConverter(),
                new IntegerToBooleanConverter()
        );
    }
}
