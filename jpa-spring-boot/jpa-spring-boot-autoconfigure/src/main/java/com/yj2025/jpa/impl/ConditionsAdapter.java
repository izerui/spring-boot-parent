package com.yj2025.jpa.impl;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从jpa的Conditions转换为jdbc的Criteria转换器
 *
 * @author liuyuhua
 */
public class ConditionsAdapter {

    private final Conditions conditions;

    /**
     * 驼峰转小写下划线
     */
    public final static Function<String, String> camelToUnderscore = fieldName -> {
        Assert.notNull(fieldName, "字段不能为空");
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    };

    public ConditionsAdapter(Conditions conditions) {
        this.conditions = conditions;
    }

    /**
     * 转换成jdbc支持的`Criteria`条件对象
     *
     * @return
     */
    public Criteria toCriteria(@Nullable Function<String, String> fieldConverter) {
        return conditions.loopContext(Criteria.empty(), null, (context, andOr, cond) -> {
            // 如果当前层级没有条件，则直接返回上下文
            if (cond.getCdList() == null || cond.getCdList().size() == 0) {
                return context;
            }
            // 构造一个内部的上下文对象
            Criteria inside = Criteria.empty();
            // 循环当前层级的所有条件，并补充内部上下文内容
            for (Conditions.Condition condition : cond.getCdList()) {
                String dbField = condition.getSqlField();
                // 当不是just模式,则进行字段转换
                if (fieldConverter != null && condition.getExpress() != null) {
                    dbField = fieldConverter.apply(condition.getField());
                }
                // 如果是just模式，则进行拆分识别出来的字段进行驼峰转下划线(或者使用自定义字段转换器转换)
                if (condition.getExpress() == null) {
                    String sqlFragment = condition.getField();
                    String regex = "\\b\\w*[A-Z]\\w*\\b(?<!'\\w*[A-Z]\\w*\\b)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(sqlFragment);
                    while (matcher.find()) {
                        String word = matcher.group();
                        String newWord = camelToUnderscore.apply(word);
                        if (fieldConverter != null) {
                            newWord = fieldConverter.apply(word);
                        }
                        sqlFragment = StringUtils.replace(sqlFragment, word, newWord);
                    }
                    inside = StringUtils.equals(condition.getAndOr(), "or") ? inside.or(Criteria.just(sqlFragment)) : inside.and(Criteria.just(sqlFragment));
                    continue;
                }
                Criteria.CriteriaStep step = StringUtils.equals(condition.getAndOr(), "or") ? inside.or(dbField) : inside.and(dbField);
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
        return Query.query(toCriteria(null));
    }

    public Query toQuery(@Nullable Function<String, String> fieldConverter) {
        return Query.query(toCriteria(fieldConverter));
    }

}
