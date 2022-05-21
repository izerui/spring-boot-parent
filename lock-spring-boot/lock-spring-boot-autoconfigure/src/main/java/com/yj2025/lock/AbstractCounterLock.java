package com.yj2025.lock;

import com.yj2025.lock.support.CompareRunnable;
import com.yj2025.lock.support.ThrowsConsumer;
import com.yj2025.lock.support.ThrowsFunction;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.atomic.PromotedToLock;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author liuyuhua
 * @date 2022/5/21
 */
public abstract class AbstractCounterLock {
    /**
     * 重试间隔
     */
    private final static int baseSleepTimeMs = 1000;
    /**
     * 重试次数
     */
    private final static int maxRetries = 10;

    protected CuratorFramework client;

    AbstractCounterLock(CuratorFramework client) {
        this.client = client;
    }

    protected DistributedAtomicLong createDistributedAtomicLong(String path) {
        DistributedAtomicLong distributedAtomicLong = new DistributedAtomicLong(
                client,
                "/counter/" + path,
                new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries),
                PromotedToLock.builder().lockPath("/counter/lock/" + path).build()
        );
        return distributedAtomicLong;
    }

    protected <T> T execute(String path, ThrowsFunction<DistributedAtomicLong, T> function) {
        try {
            DistributedAtomicLong distributedAtomicLong = createDistributedAtomicLong(path);
            return function.apply(distributedAtomicLong);
        } catch (Exception e) {
            if (e instanceof LockException) {
                throw (LockException) e;
            }
            throw new LockException(e.getMessage(), e);
        }
    }

    protected void execute(String path, ThrowsConsumer<DistributedAtomicLong> consumer) {
        try {
            DistributedAtomicLong distributedAtomicLong = createDistributedAtomicLong(path);
            consumer.accept(distributedAtomicLong);
        } catch (Exception e) {
            if (e instanceof LockException) {
                throw (LockException) e;
            }
            throw new LockException(e.getMessage(), e);
        }
    }

    public void executeCompareThan(String path, long expectedValue, CompareRunnable runnable) {
        execute(path, distributedAtomicLong -> {
            AtomicValue<Long> result = distributedAtomicLong.get();
            if (!result.succeeded()) {
                throw new LockException("[" + path + "] 计数器获取结果失败!");
            }
            long value = result.postValue();
            if (value < expectedValue) {
                runnable.lessThan();
                runnable.lessOrEqualThan();
            } else if (value == expectedValue) {
                runnable.lessOrEqualThan();
                runnable.equalThan();
                runnable.greaterOrEqualThan();
            } else {
                runnable.greaterOrEqualThan();
                runnable.greaterThan();
            }
        });
    }

}
