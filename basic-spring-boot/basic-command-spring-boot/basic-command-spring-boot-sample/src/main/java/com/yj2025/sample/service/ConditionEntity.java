package com.yj2025.sample.service;

/**
 * 执行sql实体类
 *
 * @author socik
 */
public class ConditionEntity {
    private final StringBuilder whereSql = new StringBuilder("");

    public ConditionEntity where(String sql) {
        whereSql.append(" where ").append(sql);
        return this;
    }

    public ConditionEntity eq(String sql) {
        whereSql.append(" and ").append(sql);
        return this;
    }
    public ConditionEntity or(String sql) {
        whereSql.append(" or ").append(sql);
        return this;
    }

    public ConditionEntity gt(String sql) {
        whereSql.append(" > '").append(sql).append("'");
        return this;
    }

    public ConditionEntity lt(String sql) {
        whereSql.append(" < '").append(sql).append("'");
        return this;
    }

    public ConditionEntity gte(String sql) {
        whereSql.append(" >= '").append(sql).append("'");
        return this;
    }

    public ConditionEntity lte(String sql) {
        whereSql.append(" <= '").append(sql).append("'");
        return this;
    }

    public ConditionEntity is(String sql) {
        whereSql.append(" = '").append(sql).append("'");
        return this;
    }

    public ConditionEntity notIs(String sql) {
        whereSql.append(" != '").append(sql).append("'");
        return this;
    }
    public ConditionEntity like(String sql) {
        whereSql.append(" like ").append("'%").append(sql).append("%'");
        return this;
    }

    public String build() {
        return this.whereSql.toString();
    }


}
