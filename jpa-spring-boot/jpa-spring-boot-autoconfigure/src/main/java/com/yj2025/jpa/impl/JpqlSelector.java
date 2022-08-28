package com.yj2025.jpa.impl;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class JpqlSelector {
    String fields;
    Map<Class, String> tables = new HashMap<>();
    Conditions conditions;

    private JpqlSelector() {
    }

    public static JpqlSelector create() {
        return new JpqlSelector();
    }

    public JpqlSelector withFields(String fields) {
        Assert.hasText(fields, "查询返回的字段信息必须设置");
        this.fields = fields;
        return this;
    }

    public JpqlSelector withTable(Class entityClass, String alias) {
        Assert.hasText(alias, "别名必须设置");
        Assert.notNull(entityClass, "必须指定entity类");
        Assert.isTrue(!tables.containsKey(entityClass), "已经存在相同的entity类");
        Assert.isTrue(!tables.values().contains(alias), "已经存在相同的别名");
        tables.put(entityClass, alias);
        return this;
    }

    public JpqlSelector withConditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }
}
