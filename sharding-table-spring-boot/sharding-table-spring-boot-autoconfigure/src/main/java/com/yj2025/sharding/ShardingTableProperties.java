package com.yj2025.sharding;

import lombok.Data;

import java.lang.reflect.InvocationTargetException;

@Data
public class ShardingTableProperties {
    /**
     * db中不存在指定路由表的情况下指向默认表
     */
    private String otherwise;
    /**
     * 路由规则
     */
    private AbstractRule rule;

    public void setRule(String ruleClass) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> aClass = Class.forName(ruleClass);
        this.rule = (AbstractRule) aClass.getDeclaredConstructor().newInstance();
    }

}
