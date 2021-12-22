package com.yj2025.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

import java.util.concurrent.TimeUnit;

/**
 * Created by serv on 16/8/16.
 */
public class Lock {

    private CuratorFramework client;


    Lock(CuratorFramework client) {
        this.client = client;
    }

    public <T> T execute(String lockPath, Integer leaseSeconds, LockPerform lockPerform) {

        InterProcessSemaphoreMutex semaphoreMutex = null;
        try {
            semaphoreMutex = new InterProcessSemaphoreMutex(client, "/lock/" + lockPath);
            boolean acquire = semaphoreMutex.acquire(leaseSeconds, TimeUnit.SECONDS);
            if (!acquire) {
                throw new LockException("操作同步锁定,请重试");
            }
            //执行全局唯一逻辑
            return (T) lockPerform.perform();
        } catch (Exception e) {
            throw new LockException(e.getMessage(), e);
        } finally {
            try {
                if (semaphoreMutex != null) {
                    semaphoreMutex.release();
                }
            } catch (Exception e) {
                throw new LockException(e.getMessage());
            }
        }


    }

    public <T> T execute(String lockPath, LockPerform lockPerform) {
        return execute(lockPath, 30, lockPerform);
    }

}
