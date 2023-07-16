package com.yj2025.jdbc.support;

import org.springframework.data.relational.core.query.Criteria;

import java.lang.reflect.Array;
import java.util.List;

public enum Comparator {
    EQ("=") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).is(value);
        }
    }, NEQ("!=") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).not(value);
        }
    }, BETWEEN("BETWEEN") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            if (value.getClass().isArray()) {
                return criteria.and(key).between(Array.get(value, 0), Array.get(value, 1));
            }
            if (List.class.isInstance(value)) {
                return criteria.and(key).between(((List) value).get(0), ((List) value).get(1));
            }
            throw new IllegalArgumentException("BETWEEN 的参数必须是数组或者list并且为两个值, key: " + key);
        }
    }, NOT_BETWEEN("NOT BETWEEN") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            if (value.getClass().isArray()) {
                return criteria.and(key).notBetween(Array.get(value, 0), Array.get(value, 1));
            }
            if (List.class.isInstance(value)) {
                return criteria.and(key).notBetween(((List) value).get(0), ((List) value).get(1));
            }
            throw new IllegalArgumentException("NOT_BETWEEN 的参数必须是数组或者list并且为两个值, key: " + key);
        }
    }, LT("<") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).lessThan(value);
        }
    }, LTE("<=") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).lessThanOrEquals(value);
        }
    }, GT(">") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).greaterThan(value);
        }
    }, GTE(">=") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).greaterThanOrEquals(value);
        }
    }, IS_NULL("IS NULL") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).isNull();
        }
    }, IS_NOT_NULL("IS NOT NULL") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).isNotNull();
        }
    }, LIKE("LIKE") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).like(value);
        }
    }, NOT_LIKE("NOT LIKE") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).notLike(value);
        }
    }, NOT_IN("NOT IN") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).notIn(value);
        }
    }, IN("IN") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).in(value);
        }
    }, IS_TRUE("IS TRUE") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).isTrue();
        }
    }, IS_FALSE("IS FALSE") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            return criteria.and(key).isFalse();
        }
    }, CRITERIA("Criteria Condition") {
        @Override
        protected Criteria value(Criteria criteria, String key, Object value) {
            Criteria condition = (Criteria) value;
            return criteria.and(condition);
        }
    };

    private final String comparator;

    Comparator(String comparator) {
        this.comparator = comparator;
    }

    public String getComparator() {
        return comparator;
    }

    public final Criteria join(Criteria criteria, String key, Object value) {
        if (value == null && !IS_NULL.equals(this) && !IS_NOT_NULL.equals(this) && !IS_TRUE.equals(this) && !IS_FALSE.equals(this)) {
            return criteria;
        }
        return value(criteria, key.replace(getStartExp(), ""), value);
    }

    protected abstract Criteria value(Criteria criteria, String key, Object value);

    /**
     * 使用指定的表达式表示当前查询匹配方式
     *
     * @param key
     * @return
     */
    public String wrap(String key) {
        return getStartExp().concat(key);
    }

    public String getStartExp() {
        // 例如: $EQ_、$LT_
        return "$".concat(this.name().concat("_"));
    }

    public boolean match(String key) {
        return key.startsWith(getStartExp());
    }
}