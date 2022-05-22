package com.yj2025.lock;

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
     * 计数器前缀
     */
    protected final static String COUNTER_PREFIX_PATH = "/counter/";
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
                COUNTER_PREFIX_PATH + path,
                new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries),
                PromotedToLock.builder().lockPath(COUNTER_PREFIX_PATH + "lock/" + path).build()
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

    public void runWith(String path, ThrowsConsumer<DistributedAtomicLong> consumer) {
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

    /**
     * 等待计数器直到满足条件触发true，否则等待达到超时时长触发false
     *
     * @param path             bk
     * @param waitMilliseconds 等待时长(毫秒)
     * @param predicate        条件
     * @param consumer         执行逻辑(true: 满足条件触发  false: 超时触发)
     */
    public void runWithUntil(String path, long waitMilliseconds, Predicate<Long> predicate, ThrowsConsumer<Boolean> consumer) {
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
                // 当前计数器记录的值
                long value = result.postValue().longValue();
                if (predicate.test(value)) {
                    consumer.accept(true);
                    break;
                }
                Thread.sleep(baseSleepTimeMs / 2);
            }
        });
    }

}
