package com.yj2025.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TenantConfig.class)
public class MybatisAutoConfiguration {

    @Autowired
    private TenantConfig tenantConfig;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        TenantLineHandler tenantLineHandler = new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new LongValue(1);
            }

            @Override
            public String getTenantIdColumn() {
                return tenantConfig.getField();
            }

            @Override
            public boolean ignoreTable(String tableName) {
                return tenantConfig.getIgnores().contains(tableName);
            }
        };

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (tenantConfig.isEnable()) {
            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(tenantLineHandler));
        }
        // https://baomidou.com/guide/interceptor-optimistic-locker.html#optimisticlockerinnerinterceptor
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }


//    @Bean
//    public ConfigurationCustomizer configurationCustomizer() {
//        return configuration -> configuration.setUseDeprecatedExecutor(false);
//    }

}
