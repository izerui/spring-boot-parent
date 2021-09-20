package com.yj2025.mybatis;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.Upsert;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.yj2025.mybatis.interceptor.DataPaginationInnerInterceptor;
import com.yj2025.mybatis.interceptor.TenantInterceptor;
import com.yj2025.mybatis.override.OverrideMybatisMapperRegistry;
import com.yj2025.mybatis.toolkit.ReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(TenantConfig.class)
public class MybatisAutoConfiguration {

    @Autowired
    private TenantConfig tenantConfig;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (tenantConfig.isEnable()) {
            // 租户字段校验
            interceptor.addInnerInterceptor(new TenantInterceptor(tenantConfig));
        }
        // 攻击 SQL 阻断解析器,防止全表更新与删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        // https://baomidou.com/guide/interceptor-optimistic-locker.html#optimisticlockerinnerinterceptor
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // plus 自带分页
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // 集成spring data commons分页
        interceptor.addInnerInterceptor(new DataPaginationInnerInterceptor());
        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            ReflectionUtil.setPropertyValue(
                    MybatisConfiguration.class,
                    configuration,
                    "mybatisMapperRegistry",
                    new OverrideMybatisMapperRegistry(configuration));
        };
    }

    /**
     * 这里备注下,如果需要扩展basemapper新的方法注入器,修改 {@link DefaultSqlInjector#getMethodList(Class, TableInfo)} 返回值添加新的注入器
     * <code>
     *     return new DefaultSqlInjector() {
     *          @Override
     *          public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
     *            List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
     *            methodList.add(new Upsert());
     *            return methodList;
     *            }
     *      };
     * </code>
     * @return
     */
    @Bean
    public DefaultSqlInjector defaultSqlInjector() {
        return new DefaultSqlInjector();
    }

}
