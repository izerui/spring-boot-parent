package com.yj2025.lock;

import com.yj2025.lock.support.ThrowsRunnable;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by serv on 16/8/16.
 */
public class Lock implements DisposableBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(Lock.class);

    private final static Integer LEASE_SECONDS = 30;

    private CuratorFramework client;


    Lock(CuratorFramework client) {
        this.client = client;
    }

    public void execute(String lockPath, Integer leaseSeconds, ThrowsRunnable runnable, Function<LockException, RuntimeException> catchThrowNew) {
        InterProcessSemaphoreMutex semaphoreMutex = null;
        try {
            semaphoreMutex = new InterProcessSemaphoreMutex(client, "/lock/" + lockPath);
            boolean acquire = semaphoreMutex.acquire(leaseSeconds, TimeUnit.SECONDS);
            if (!acquire) {
                throw new LockException("操作同步锁定,请重试");
            }
            //执行全局唯一逻辑
            runnable.run();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (e instanceof LockException) {
                throw catchThrowNew.apply((LockException) e);
            } else {
                throw catchThrowNew.apply(new LockException(e.getMessage(), e));
            }
        } finally {
            try {
                if (semaphoreMutex != null) {
                    semaphoreMutex.release();
                }
            } catch (Exception e) {
                throw catchThrowNew.apply(new LockException("释放锁失败: " + e.getMessage() + " lockId: " + lockPath));
            }
        }
    }

    public void execute(String lockPath, Integer leaseSeconds, ThrowsRunnable runnable) {
        execute(lockPath, leaseSeconds, runnable, e -> e);
    }

    public void execute(String lockPath, ThrowsRunnable runnable) {
        execute(lockPath, LEASE_SECONDS, () -> runnable.run(), e -> e);
    }

    public void execute(String lockPath, ThrowsRunnable runnable, Function<LockException, RuntimeException> catchThrowNew) {
        execute(lockPath, LEASE_SECONDS, () -> runnable.run(), catchThrowNew);
    }


    @Override
    public void destroy() {
        client.close();
    }

}
