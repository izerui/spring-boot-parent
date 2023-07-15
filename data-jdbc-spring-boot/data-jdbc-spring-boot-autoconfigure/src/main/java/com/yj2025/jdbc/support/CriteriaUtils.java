package com.yj2025.jdbc.support;

import com.google.common.base.CaseFormat;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.Assert;

import java.util.Map;

public final class CriteriaUtils {

    /**
     * 通过map连接到指定的criteria实例
     *
     * @param criteria 要连接的实例
     * @param map      连接的kv映射 , key会进行相应的替换，和驼峰转下划线
     * @return 返回一个新的criteria实例
     */
    public static Criteria joinToCriteria(Criteria criteria, Map<String, Object> map) {
        Assert.notNull(map, "map must not be null");
        for (String key : map.keySet()) {
            criteria = join(criteria, key, map.get(key));
        }
        return criteria;
    }

    /**
     * 使用指定的表达式表示当前查询匹配方式
     *
     * @param key
     * @param comparator
     * @return
     */
    public static String wrap(String key, Comparator comparator) {
        return comparator.wrap(key);
    }

    private static Criteria join(Criteria criteria, String key, Object value) {
        if (key.startsWith("$")) {
            for (Comparator comparator : Comparator.values()) {
                if (comparator.match(key)) {
                    return comparator.join(criteria, key, value);
                }
            }
        }
        if (value == null) {
            return criteria.and(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key)).isNull();
        } else {
            return criteria.and(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key)).is(value);
        }
    }

}
