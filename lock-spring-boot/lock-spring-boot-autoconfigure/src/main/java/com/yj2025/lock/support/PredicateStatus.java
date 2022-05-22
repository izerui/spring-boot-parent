package com.yj2025.lock.support;

public final class PredicateStatus {

    private final boolean satisfy;
    private final long counterValue;
    private final long timeMillis;

    public PredicateStatus(boolean satisfy, long counterValue, long beginTimeMillis) {
        this.satisfy = satisfy;
        this.counterValue = counterValue;
        this.timeMillis = System.currentTimeMillis() - beginTimeMillis;
    }

    /**
     * 是否满足条件
     *
     * @return
     */
    public boolean isSatisfy() {
        return this.satisfy;
    }

    /**
     * 获取计数器的值
     *
     * @return
     */
    public long getCounterValue() {
        return this.counterValue;
    }

    /**
     * 耗时
     *
     * @return
     */
    public long getTimeMillis() {
        return this.timeMillis;
    }
}
