package com.yj2025.jdbc.dialect.flag;

import java.util.ArrayList;
import java.util.List;

public class QueryFlagThreadLocalHolder {

    private final static ThreadLocal<List<QueryFlag>> QUERY_FLAG_LOCALS = new InheritableThreadLocal<>();

    public static void setQueryFlags(List<QueryFlag> values) {
        QUERY_FLAG_LOCALS.set(values);
    }

    public static void removeQueryFlags() {
        QUERY_FLAG_LOCALS.remove();
    }

    public static List<QueryFlag> getQueryFlags() {
        return QUERY_FLAG_LOCALS.get();
    }

}
