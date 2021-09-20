package com.yj2025.mybatis.override;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态构造sql注入器
 */
public class OverrideDefaultSqlInjector extends DefaultSqlInjector {

    private List<? extends AbstractMethod> extandsMethodList;

    public OverrideDefaultSqlInjector(List<? extends AbstractMethod> extandsMethodList) {
        this.extandsMethodList = extandsMethodList;
    }

    /**
     * 增加自定义的注入方法类
     * 目的: 返回自定义的 sql 注入器 并返回修饰后的 {@link org.apache.ibatis.mapping.MappedStatement} 主要用来动态修改实际执行的 sql,参数,返回类型等
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.addAll(extandsMethodList);
        return methodList;
    }
}
