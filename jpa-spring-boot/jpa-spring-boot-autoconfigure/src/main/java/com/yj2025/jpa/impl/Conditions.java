package com.yj2025.jpa.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Conditions implements Cloneable {

    private Logger logger = LoggerFactory.getLogger(Conditions.class);

    private List<Condition> cdList = new ArrayList<>();

    private List<CombCondition> combList = new ArrayList<>();

    public Conditions() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static Conditions empty() {
        return new Conditions();
    }

    public static Conditions where() {
        return new Conditions();
    }

    public static Conditions where(String field) {
        Conditions root = where();
        root.cdList.add(new Condition(true, "", field));
        return root;
    }

    public static Conditions where(boolean valid, String field) {
        Conditions root = where();
        root.cdList.add(new Condition(valid, "", field));
        return root;
    }

    public static Conditions where(Supplier<Boolean> valid, String field) {
        return where(valid.get(), field);
    }

    public Conditions and(String field) {
        cdList.add(new Condition(true, cdList.isEmpty() ? "" : "and", field));
        return this;
    }

    public Conditions and(boolean valid, String field) {
        cdList.add(new Condition(valid, cdList.isEmpty() ? "" : "and", field));
        return this;
    }

    public Conditions and(Supplier<Boolean> valid, String field) {
        return and(valid.get(), field);
    }

    public Conditions or(String field) {
        cdList.add(new Condition(true, cdList.isEmpty() ? "" : "or", field));
        return this;
    }

    public Conditions or(boolean valid, String field) {
        cdList.add(new Condition(valid, cdList.isEmpty() ? "" : "or", field));
        return this;
    }

    public Conditions or(Supplier<Boolean> valid, String field) {
        return or(valid.get(), field);
    }

    public Conditions and(Conditions conditions) {
        this.combList.add(new CombCondition("and", conditions));
        return this;
    }

    public Conditions and(boolean valid, Supplier<Conditions> conditions) {
        if (valid) {
            this.combList.add(new CombCondition("and", conditions.get()));
        }
        return this;
    }

    public Conditions and(Supplier<Boolean> valid, Supplier<Conditions> conditions) {
        return and(valid.get(), conditions);
    }

    public Conditions or(Conditions conditions) {
        this.combList.add(new CombCondition("or", conditions));
        return this;
    }

    public Conditions or(boolean valid, Supplier<Conditions> conditions) {
        if (valid) {
            this.combList.add(new CombCondition("or", conditions.get()));
        }
        return this;
    }

    public Conditions or(Supplier<Boolean> valid, Supplier<Conditions> conditions) {
        return or(valid.get(), conditions);
    }

    private Condition lastCondition() {
        return this.cdList.get(cdList.size() - 1);
    }

    public Conditions is(Object value) {
        lastCondition().express("=").value(value);
        return this;
    }

    public Conditions isNull() {
        lastCondition().express("is null");
        return this;
    }

    public Conditions notNull() {
        lastCondition().express("is not null");
        return this;
    }

    public Conditions notIn(Object value) {
        lastCondition().express("not in").value(value);
        return this;
    }

    public Conditions like(Object value) {
        lastCondition().express("like").value(value);
        return this;
    }

    public Conditions gt(Object value) {
        lastCondition().express(">").value(value);
        return this;
    }

    public Conditions lt(Object value) {
        lastCondition().express("<").value(value);
        return this;
    }

    public Conditions gte(Object value) {
        lastCondition().express(">=").value(value);
        return this;
    }

    public Conditions lte(Object value) {
        lastCondition().express("<=").value(value);
        return this;
    }

    public Conditions ne(Object value) {
        lastCondition().express("<>").value(value);
        return this;
    }

    public Conditions in(Object value) {
        lastCondition().express("in").value(value);
        return this;
    }


    @Override
    public String toString() {
        return toQL(new HashMap<>());
    }

    public String toQL(Map<String, Object> params) {
        Assert.notNull(params, "参数对象不能为空");
        StringBuilder sb = new StringBuilder("");
        if (cdList == null || cdList.size() == 0) {
            return "";
        }
        sb.append(" ( ");
        for (Condition condition : cdList) {
            sb.append(condition.toQL(params));
        }

        if (combList != null) {

            for (CombCondition comb : combList) {
                sb.append(comb.andOr);
                sb.append(comb.toQL(params));
            }
        }

        sb.append(") ");

        String ql = sb.toString();
//        logger.debug(ql);
        return ql;
    }

    private static class CombCondition {
        private String andOr;
        private Conditions cds;

        public CombCondition(String andOr, Conditions cds) {
            this.andOr = andOr;
            this.cds = cds;
        }

        public String getAndOr() {
            return andOr;
        }

        public void setAndOr(String andOr) {
            this.andOr = andOr;
        }

        public Conditions getCds() {
            return cds;
        }

        private String toQL(Map<String, Object> params) {
            return cds.toQL(params);
        }

    }

    private static class Condition {
        private boolean valid;
        private String andOr;
        //查询字段
        private String field;
        //表达式
        private String express;
        //值
        private Object value;

        private Condition(boolean valid, String andOr, String field) {
            this.valid = valid;
            this.andOr = andOr;
            this.field = field;
        }

        private String toQL(Map<String, Object> params) {
            if (!isValid()) {
                return isEmpty(andOr) ? "1=1 " : andOr + " 1=1 ";
            }
            int index = 0;
            String fieldValueKey = StringUtils.replace(this.field, ".", "_");
            String paramsKey = fieldValueKey + "_" + index;
            while (params.containsKey(paramsKey)) {
                index++;
                paramsKey = fieldValueKey + "_" + index;
            }
            params.put(paramsKey, value);

            if (isEmpty(andOr)) {
                return field + " " + (express != null ? express : "") + (value != null ? " :" + paramsKey : "") + " ";
            } else {
                return andOr + " " + field + " " + (express != null ? express : "") + (value != null ? " :" + paramsKey : "") + " ";
            }
        }

        private Condition express(String express) {
            this.express = express;
            return this;
        }

        private Condition value(Object value) {
            this.value = value;
            return this;
        }

        public String getAndOr() {
            return andOr;
        }

        public String getField() {
            return field;
        }

        public String getExpress() {
            return express;
        }

        public Object getValue() {
            return value;
        }

        public boolean isValid() {
            return valid;
        }

    }

}
