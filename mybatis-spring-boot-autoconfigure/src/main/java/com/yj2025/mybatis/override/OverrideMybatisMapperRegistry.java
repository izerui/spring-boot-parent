package com.yj2025.mybatis.override;

import com.baomidou.mybatisplus.core.MybatisMapperAnnotationBuilder;
import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxyFactory;
import com.yj2025.mybatis.toolkit.ReflectionUtil;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

public class OverrideMybatisMapperRegistry extends MybatisMapperRegistry {

    private Configuration config;
    private Map<Class<?>, MybatisMapperProxyFactory<?>> knownMappers;

    public OverrideMybatisMapperRegistry(Configuration config) {
        super(config);
        this.config = config;
        this.knownMappers = ReflectionUtil.getPropertyValue(MybatisMapperRegistry.class, this, "knownMappers");
    }


    @Override
    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                // TODO 如果之前注入 直接返回
                return;
                // TODO 这里就不抛异常了
//                throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
            }
            boolean loadCompleted = false;
            try {
                // TODO 这里也换成 MybatisMapperProxyFactory 而不是 MapperProxyFactory
                knownMappers.put(type, new OverrideMybatisMapperProxyFactory<>(type));
                // It's important that the type is added before the parser is run
                // otherwise the binding may automatically be attempted by the
                // mapper parser. If the type is already known, it won't try.
                // TODO 这里也换成 MybatisMapperAnnotationBuilder 而不是 MapperAnnotationBuilder
                MybatisMapperAnnotationBuilder parser = new MybatisMapperAnnotationBuilder(config, type);
                parser.parse();
                loadCompleted = true;
            } finally {
                if (!loadCompleted) {
                    knownMappers.remove(type);
                }
            }
        }
    }

}
