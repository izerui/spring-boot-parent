package com.yj2025.mybatis.override;

import com.baomidou.mybatisplus.core.override.MybatisMapperMethod;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yj2025.mybatis.toolkit.ReflectionUtil;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.util.ReflectionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 重写 {@link MybatisMapperProxy} 目的是为了替换 {@link OverrideMybatisMapperMethod} mapper方法执行器
 *
 * @param <T>
 */
public class OverrideMybatisMapperProxy<T> extends MybatisMapperProxy<T> {

    private SqlSession sqlSession;
    private Map<Method, MapperMethodInvoker> cacheMethods;
    private Class<T> mapperInterface;

    public OverrideMybatisMapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethodInvoker> cacheMethods) {
        super(sqlSession, mapperInterface, null);
        this.sqlSession = sqlSession;
        this.cacheMethods = cacheMethods;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else {
                return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    private MapperMethodInvoker cachedInvoker(Method method) throws Throwable {
        try {
            return CollectionUtils.computeIfAbsent(cacheMethods, method, m -> {
                if (m.isDefault()) {
                    try {
                        Method privateLookupInMethod = ReflectionUtil.getPropertyValue(OverrideMybatisMapperProxy.class, this, "privateLookupInMethod");
                        if (privateLookupInMethod == null) {
                            Method handleJava8 = ReflectionUtils.findMethod(MybatisMapperProxy.class, "getMethodHandleJava8", Method.class);
                            handleJava8.setAccessible(true);
                            return new DefaultMethodInvoker((MethodHandle) handleJava8.invoke(this, method));
                        } else {
                            Method handleJava9 = ReflectionUtils.findMethod(MybatisMapperProxy.class, "getMethodHandleJava9", Method.class);
                            handleJava9.setAccessible(true);
                            return new DefaultMethodInvoker((MethodHandle) handleJava9.invoke(this, method));
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return new PlainMethodInvoker(new OverrideMybatisMapperMethod(mapperInterface, method, sqlSession.getConfiguration()));
                }
            });
        } catch (RuntimeException re) {
            Throwable cause = re.getCause();
            throw cause == null ? re : cause;
        }
    }

    interface MapperMethodInvoker {
        Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable;
    }

    private static class PlainMethodInvoker implements MapperMethodInvoker {
        private final MybatisMapperMethod mapperMethod;

        public PlainMethodInvoker(MybatisMapperMethod mapperMethod) {
            super();
            this.mapperMethod = mapperMethod;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
            return mapperMethod.execute(sqlSession, args);
        }
    }

    private static class DefaultMethodInvoker implements MapperMethodInvoker {
        private final MethodHandle methodHandle;

        public DefaultMethodInvoker(MethodHandle methodHandle) {
            super();
            this.methodHandle = methodHandle;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
            return methodHandle.bindTo(proxy).invokeWithArguments(args);
        }
    }
}
