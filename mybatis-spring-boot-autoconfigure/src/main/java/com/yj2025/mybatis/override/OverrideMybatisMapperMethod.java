package com.yj2025.mybatis.override;

import com.baomidou.mybatisplus.core.override.MybatisMapperMethod;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.yj2025.mybatis.toolkit.ReflectionUtil;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Method;
import java.util.List;

public class OverrideMybatisMapperMethod extends MybatisMapperMethod {

    private final static ThreadLocal<Long> COUNT_THREAD_LOCAL = new ThreadLocal<>();

    private MapperMethod.SqlCommand command;
    private MapperMethod.MethodSignature method;

    public OverrideMybatisMapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
        super(mapperInterface, method, config);
        this.command = ReflectionUtil.getPropertyValue(MybatisMapperMethod.class, this, "command");
        this.method = ReflectionUtil.getPropertyValue(MybatisMapperMethod.class, this, "method");
    }

    @Override
    public Object execute(SqlSession sqlSession, Object[] args) {
        if (method.getReturnType().isAssignableFrom(PageImpl.class)) {
            Assert.notNull(command.getType(), "Unknown execution method for: " + command.getName());
            if (command.getType().equals(SqlCommandType.SELECT)) {
                return executeForPage(sqlSession, args);
            }
        }
        return super.execute(sqlSession, args);
    }

    private <E> Object executeForPage(SqlSession sqlSession, Object[] args) {
        PageRequest request = null;
        for (Object arg : args) {
            if (arg instanceof PageRequest) {
                request = (PageRequest) arg;
                break;
            }
        }
        Assert.notNull(request, "can't found PageRequest for args!");
        Object param = method.convertArgsToSqlCommandParam(args);
        List<E> list = sqlSession.selectList(command.getName(), param);
        return new PageImpl<>(list, request, getCountFromLocalThread());
    }

    public static long getCountFromLocalThread() {
        Long count = COUNT_THREAD_LOCAL.get();
        if (count == null) {
            count = 0L;
        }
        return count;
    }

    public static void setCountToLocalThread(long value) {
        COUNT_THREAD_LOCAL.set(value);
    }
}
