package com.yj2025.lock;

import com.yj2025.lock.support.ThrowsConsumer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.springframework.beans.factory.DisposableBean;

import java.util.function.Predicate;

/**
 * @author liuyuhua
 * @date 2022/5/21
 */
public class CounterLock extends AbstractCounterLock implements DisposableBean {

    CounterLock(CuratorFramework client) {
        super(client);
    }

    /**
     * 初始化一个计数器,默认值为0(可选初始化)
     *
     * @param path 业务path
     */
    public void initialize(String path) {
        runWith(path, distributedAtomicLong -> {
            boolean initialize = distributedAtomicLong.initialize(0L);
            if (!initialize) {
                throw new LockException("[" + path + "] 计数器已存在或者初始化0失败!");
            }
        });
    }

    /**
     * 删除计数器
     *
     * @param path
     */
    public void delete(String path) {
        try {
            client.delete().forPath(COUNTER_PREFIX_PATH + path);
        } catch (Exception e) {
            throw new LockException(e.getMessage(), e);
        }
    }


    /**
     * 计数器加1
     *
     * @param path 业务path
     */
    public AtomicValue<Long> increment(String path) {
        return runWith(path, distributedAtomicLong -> {
            AtomicValue<Long> result = distributedAtomicLong.increment();
            if (!result.succeeded()) {
                throw new LockException("[" + path + "] 计数器增加1计数操作,重试10次后最终失败!");
            }
            return result;
        });
    }

    /**
     * 计数器增加指定数值
     *
     * @param path 业务path
     */
    public AtomicValue<Long> add(String path, Long delta) {
        return runWith(path, distributedAtomicLong -> {
            AtomicValue<Long> result = distributedAtomicLong.add(delta);
            if (!result.succeeded()) {
                throw new LockException("[" + path + "] 计数器增加" + delta + "计数操作,重试10次后最终失败!");
            }
            return result;
        });
    }

    /**
     * 计数器减1
     *
     * @param path 业务path
     */
    public AtomicValue<Long> decrement(String path) {
        return runWith(path, distributedAtomicLong -> {
            AtomicValue<Long> result = distributedAtomicLong.decrement();
            if (!result.succeeded()) {
                throw new LockException("[" + path + "] 计数器减少1计数操作,重试10次后最终失败!");
            }
            return result;
        });
    }

    /**
     * 计数器减少指定数值
     *
     * @param path 业务path
     */
    public AtomicValue<Long> subtract(String path, Long delta) {
        return runWith(path, distributedAtomicLong -> {
            AtomicValue<Long> result = distributedAtomicLong.subtract(delta);
            if (!result.succeeded()) {
                throw new LockException("[" + path + "] 计数器减少" + delta + "计数操作,重试10次后最终失败!");
            }
            return result;
        });
    }


    /**
     * 开启一个异步线程等待计数器直到满足条件触发true，否则等待达到超时时长触发false
     *
     * @param path             bk
     * @param waitMilliseconds 等待时长(毫秒)
     * @param predicate        条件
     * @param consumer         执行逻辑(true: 满足条件触发  false: 超时触发)
     */
    public void runWithAsyncUntil(String path, long waitMilliseconds, Predicate<Long> predicate, ThrowsConsumer<Boolean> consumer) {
        new Thread(() -> runWithUntil(path, waitMilliseconds, predicate, consumer)).start();
    }


    @Override
    public void destroy() throws Exception {
        client.close();
    }
}
