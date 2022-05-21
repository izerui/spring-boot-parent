package com.yj2025.lock;

import com.yj2025.lock.support.CompareRunnable;
import com.yj2025.lock.support.ThrowsConsumer;
import com.yj2025.lock.support.ThrowsFunction;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.atomic.PromotedToLock;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.function.Predicate;

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

    protected <T> T runWith(String path, ThrowsFunction<DistributedAtomicLong, T> function) {
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

    protected void runWith(String path, ThrowsConsumer<DistributedAtomicLong> consumer) {
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
        runWith(path, distributedAtomicLong -> {
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

    /**
     * 等待计数器直到满足条件触发true，否则等待达到超时时长触发false
     *
     * @param path             bk
     * @param waitMilliseconds 等待时长(毫秒)
     * @param predicate        条件
     * @param consumer         执行逻辑(true: 满足条件触发  false: 超时触发)
     */
    public void runWithWaitUntil(String path, long waitMilliseconds, Predicate<Long> predicate, ThrowsConsumer<Boolean> consumer) {
        runWith(path, distributedAtomicLong -> {
            long beginTimeMillis = System.currentTimeMillis();
            long expirationTimeMillis = beginTimeMillis + waitMilliseconds;
            while (true) {
                if (System.currentTimeMillis() > expirationTimeMillis) {
//                    System.out.println(System.currentTimeMillis() + " - " + expirationTimeMillis + " - " + beginTimeMillis);
                    consumer.accept(false);
                    break;
                }
                AtomicValue<Long> result = distributedAtomicLong.get();
                if (!result.succeeded()) {
                    throw new LockException("[" + path + "] 计数器获取结果失败!");
                }
                if (predicate.test(result.postValue().longValue())) {
                    consumer.accept(true);
                    break;
                }
                Thread.sleep(baseSleepTimeMs / 2);
            }
        });
    }

}
