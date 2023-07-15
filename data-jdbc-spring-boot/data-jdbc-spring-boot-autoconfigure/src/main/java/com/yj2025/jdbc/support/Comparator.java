package com.yj2025.jdbc.support;

import com.google.common.base.CaseFormat;
import org.springframework.data.relational.core.query.Criteria;

import java.lang.reflect.Array;
import java.util.List;

public enum Comparator {
    EQ("=") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).is(value);
        }
    }, NEQ("!=") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).not(value);
        }
    }, BETWEEN("BETWEEN") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            if (value.getClass().isArray()) {
                return criteria.and(replacedLowerUnderscoreKey).between(Array.get(value, 0), Array.get(value, 1));
            }
            if (value.getClass().isAssignableFrom(List.class)) {
                return criteria.and(replacedLowerUnderscoreKey).between(((List) value).get(0), ((List) value).get(1));
            }
            throw new IllegalArgumentException("BETWEEN 的参数必须是数组或者list并且为两个值, key: " + replacedLowerUnderscoreKey);
        }
    }, NOT_BETWEEN("NOT BETWEEN") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            if (value.getClass().isArray()) {
                return criteria.and(replacedLowerUnderscoreKey).notBetween(Array.get(value, 0), Array.get(value, 1));
            }
            if (value.getClass().isAssignableFrom(List.class)) {
                return criteria.and(replacedLowerUnderscoreKey).notBetween(((List) value).get(0), ((List) value).get(1));
            }
            throw new IllegalArgumentException("NOT_BETWEEN 的参数必须是数组或者list并且为两个值, key: " + replacedLowerUnderscoreKey);
        }
    }, LT("<") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).lessThan(value);
        }
    }, LTE("<=") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).lessThanOrEquals(value);
        }
    }, GT(">") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).greaterThan(value);
        }
    }, GTE(">=") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).greaterThanOrEquals(value);
        }
    }, IS_NULL("IS NULL") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).isNull();
        }
    }, IS_NOT_NULL("IS NOT NULL") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).isNotNull();
        }
    }, LIKE("LIKE") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).like(value);
        }
    }, NOT_LIKE("NOT LIKE") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).notLike(value);
        }
    }, NOT_IN("NOT IN") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).notIn(value);
        }
    }, IN("IN") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).in(value);
        }
    }, IS_TRUE("IS TRUE") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).isTrue();
        }
    }, IS_FALSE("IS FALSE") {
        @Override
        protected Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value) {
            return criteria.and(replacedLowerUnderscoreKey).isFalse();
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
        return value(criteria, CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key.replace(getStartExp(), "")), value);
    }

    protected abstract Criteria value(Criteria criteria, String replacedLowerUnderscoreKey, Object value);

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