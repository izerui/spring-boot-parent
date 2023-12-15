package com.yj2025.jpa.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;

/**
 * 从jpa的Conditions转换为jdbc的Criteria转换器
 * @author liuyuhua
 */
public class ConditionsAdapter {

    private final Conditions conditions;

    public ConditionsAdapter(Conditions conditions) {
        this.conditions = conditions;
    }

    /**
     * 转换成jdbc支持的`Criteria`条件对象
     *
     * @return
     */
    public Criteria toCriteria() {
        return conditions.loopContext(Criteria.empty(), null, (context, andOr, cond) -> {
            // 如果当前层级没有条件，则直接返回上下文
            if (cond.getCdList() == null || cond.getCdList().size() == 0) {
                return context;
            }
            // 构造一个内部的上下文对象
            Criteria inside = Criteria.empty();
            // 循环当前层级的所有条件，并补充内部上下文内容
            for (Conditions.Condition condition : cond.getCdList()) {
                Criteria.CriteriaStep step = StringUtils.equals(condition.getAndOr(), "or") ? inside.or(condition.getSqlField()) : inside.and(condition.getSqlField());
                inside = switch (condition.getExpress()) {
                    case "=" -> step.is(condition.getValue());
                    case "is null" -> step.isNull();
                    case "is not null" -> step.isNotNull();
                    case "not in" -> step.notIn(condition.getValue());
                    case "like" -> step.like(condition.getValue());
                    case ">" -> step.greaterThan(condition.getValue());
                    case "<" -> step.lessThan(condition.getValue());
                    case ">=" -> step.greaterThanOrEquals(condition.getValue());
                    case "<=" -> step.lessThanOrEquals(condition.getValue());
                    case "<>" -> step.not(condition.getValue());
                    case "in" -> step.in(condition.getValue());
                    default -> throw new RuntimeException("不支持的express条件: " + condition.getExpress());
                };
            }
            // 根据连接符与外部的上下文进行连接，并返回完善后的上下文
            return StringUtils.equals(andOr, "or") ? context.or(inside) : context.and(inside);
        });
    }

    public Query toQuery() {
        return Query.query(toCriteria());
    }

}
