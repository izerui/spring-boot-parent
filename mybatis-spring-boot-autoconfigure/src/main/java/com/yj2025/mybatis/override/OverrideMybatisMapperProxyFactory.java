package com.yj2025.mybatis.override;

import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxyFactory;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mapperProxy 工厂类
 */
public class OverrideMybatisMapperProxyFactory<T> extends MybatisMapperProxyFactory<T> {

    private final Map<Method, OverrideMybatisMapperProxy.MapperMethodInvoker> cacheMethods = new ConcurrentHashMap<>();

    public OverrideMybatisMapperProxyFactory(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    public T newInstance(SqlSession sqlSession) {
        final MybatisMapperProxy<T> mapperProxy = new OverrideMybatisMapperProxy<>(sqlSession, getMapperInterface(), cacheMethods);
        return newInstance(mapperProxy);
    }
}
