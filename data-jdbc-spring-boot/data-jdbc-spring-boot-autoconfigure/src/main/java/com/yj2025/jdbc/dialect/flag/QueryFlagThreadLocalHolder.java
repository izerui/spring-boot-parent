package com.yj2025.jdbc.dialect.flag;

public class QueryFlagThreadLocalHolder {

    private final static ThreadLocal<String> THREAD_LOCAL0 = new InheritableThreadLocal<>();
    private final static ThreadLocal<Boolean> THREAD_LOCAL1 = new InheritableThreadLocal<>();
    private final static ThreadLocal<Boolean> THREAD_LOCAL2 = new InheritableThreadLocal<>();

    public static void setQueryFlag(String queryFlag) {
        THREAD_LOCAL0.set(queryFlag);
    }

    public static String getQueryFlag() {
        return THREAD_LOCAL0.get();
    }

    public static void setComment(boolean isComment) {
        THREAD_LOCAL1.set(isComment);
    }

    public static boolean isComment() {
        return THREAD_LOCAL1.get() != null ? THREAD_LOCAL1.get() : false;
    }

}
