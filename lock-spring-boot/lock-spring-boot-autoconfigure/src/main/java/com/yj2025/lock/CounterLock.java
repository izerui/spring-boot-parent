package com.yj2025.lock;

import com.yj2025.lock.support.CompareRunnable;
import com.yj2025.lock.support.ThrowsRunnable;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.springframework.beans.factory.DisposableBean;

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
        execute(path, distributedAtomicLong -> {
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
            client.delete().forPath("/counter/" + path);
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
        return execute(path, distributedAtomicLong -> {
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
        return execute(path, distributedAtomicLong -> {
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
        return execute(path, distributedAtomicLong -> {
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
        return execute(path, distributedAtomicLong -> {
            AtomicValue<Long> result = distributedAtomicLong.subtract(delta);
            if (!result.succeeded()) {
                throw new LockException("[" + path + "] 计数器减少" + delta + "计数操作,重试10次后最终失败!");
            }
            return result;
        });
    }

    /**
     * 判断计数器如果小于指定数值的情况下
     */
    public void executeLessThan(String path, long expectedValue, ThrowsRunnable runnable) {
        executeCompareThan(path, expectedValue, new CompareRunnable() {
            @Override
            public void lessThan() throws Exception {
                runnable.run();
            }
        });
    }

    /**
     * 判断计数器如果小于等于指定数值的情况下
     */
    public void executeLessOrEqualThan(String path, long expectedValue, ThrowsRunnable runnable) {
        executeCompareThan(path, expectedValue, new CompareRunnable() {
            @Override
            public void lessOrEqualThan() throws Exception {
                runnable.run();
            }
        });
    }

    /**
     * 判断计数器如果等于指定数值的情况下
     */
    public void executeEqualThan(String path, long expectedValue, ThrowsRunnable runnable) {
        executeCompareThan(path, expectedValue, new CompareRunnable() {
            @Override
            public void equalThan() throws Exception {
                runnable.run();
            }
        });
    }

    /**
     * 判断计数器如果大于等于指定数值的情况下
     */
    public void executeGreaterOrEqualThan(String path, long expectedValue, ThrowsRunnable runnable) {
        executeCompareThan(path, expectedValue, new CompareRunnable() {
            @Override
            public void greaterOrEqualThan() throws Exception {
                runnable.run();
            }
        });
    }

    /**
     * 判断计数器如果大于指定数值的情况下
     */
    public void executeGreaterThan(String path, long expectedValue, ThrowsRunnable runnable) {
        executeCompareThan(path, expectedValue, new CompareRunnable() {
            @Override
            public void greaterThan() throws Exception {
                runnable.run();
            }
        });
    }


    @Override
    public void destroy() throws Exception {
        client.close();
    }
}
