package com.ecworking.jpa;

import com.ecworking.jpa.impl.PlatformRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by serv on 2014/10/11.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@EnableJpaRepositories(repositoryBaseClass = PlatformRepositoryImpl.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class JpaConfiguration {

}
