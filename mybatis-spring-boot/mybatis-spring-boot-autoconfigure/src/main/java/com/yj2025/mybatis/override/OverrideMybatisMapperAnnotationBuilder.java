package com.yj2025.mybatis.override;

import com.baomidou.mybatisplus.core.MybatisMapperAnnotationBuilder;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.Configuration;
import org.springframework.data.domain.Page;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 修改mapper指定返回类型的方法,执行{@link org.apache.ibatis.session.SqlSession} 原生mybatis方法的时候,返回值类型
 */
public class OverrideMybatisMapperAnnotationBuilder extends MybatisMapperAnnotationBuilder {

    private Class<?> type;

    public OverrideMybatisMapperAnnotationBuilder(Configuration configuration, Class<?> type) {
        super(configuration, type);
        this.type = type;
    }

    @Override
    protected Class<?> getReturnType(Method method) {
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, type);
        if (resolvedReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
            Class<?> rawType = (Class) parameterizedType.getRawType();
            Type[] actualTypeArguments;
            Type returnTypeParameter;
            if (!Collection.class.isAssignableFrom(rawType) && !Cursor.class.isAssignableFrom(rawType)) {
                if (Page.class.isAssignableFrom(rawType)) {
                    actualTypeArguments = parameterizedType.getActualTypeArguments();
                    returnTypeParameter = actualTypeArguments[0];
                    if (returnTypeParameter instanceof Class) {
                        return (Class) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        return (Class) ((ParameterizedType) returnTypeParameter).getRawType();
                    }
                }
            }
        }
        return super.getReturnType(method);
    }
}
