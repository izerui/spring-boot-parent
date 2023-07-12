package com.yj2025.jdbc.utils;

import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.Assert;

import java.util.Map;

public final class CriteriaUtils {

    /**
     * 通过map连接到指定的criteria实例
     * @param criteria 要连接的实例
     * @param map 连接的kv映射
     * @return 返回一个新的criteria实例
     */
    public static Criteria joinToCriteria(Criteria criteria, Map<String, Object> map) {
        Assert.notNull(map, "map must not be null");
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value == null) {
                criteria = criteria.and(key).isNull();
            } else {
                criteria = criteria.and(key).is(value);
            }
        }
        return criteria;
    }
}
