package com.yj2025.jdbc;

import com.yj2025.jdbc.dialect.flag.QueryFlagMethodAspect;
import com.yj2025.jdbc.impl.PlatformJdbcRepositoryImpl;
import com.yj2025.jdbc.override.OverrideDefaultNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * callback: https://docs.spring.io/spring-data/relational/reference/jdbc/events.html#jdbc.entity-callbacks
 * @see org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration
 */
@Slf4j
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJdbcRepositories(repositoryBaseClass = PlatformJdbcRepositoryImpl.class)
public class DataJdbcConfiguration {

    private final ApplicationContext applicationContext;

    public DataJdbcConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public QueryFlagMethodAspect queryFlagMethodAspect() {
        return new QueryFlagMethodAspect(applicationContext);
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

//    @Bean
//    public BeanDefinitionCustomizer customizer() {
//        log.info("使用 org.springframework.beans.factory.config.BeanDefinitionCustomizer 方式进行bean替换");
//        return bd -> {
//            if (DataSourceTransactionManager.class.getName().equals(bd.getBeanClassName())) {
//                bd.setPrimary(true);
//            }
//        };
//    }

}
