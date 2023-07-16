package com.yj2025.jdbc.dialect.flag;

public class QueryFlagThreadLocalHolder {

    private final static ThreadLocal<String> THREAD_LOCAL = new InheritableThreadLocal<>();

    public static void setQueryFlag(String queryFlag) {
        THREAD_LOCAL.set(queryFlag);
    }

    public static String getQueryFlag() {
        return THREAD_LOCAL.get();
    }

}
