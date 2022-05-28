package com.yj2025.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by serv on 16/8/16.
 */
@Configuration
public class LockConfiguration {

    @Bean
    public CuratorFramework curatorFramework(@Value("${zookeeper.connectionString}") String zkConnectionString) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkConnectionString, new RetryForever(100));
        curatorFramework.start();
        return curatorFramework;
    }

    @Bean
    public Lock lock(CuratorFramework curatorFramework) {
        return new Lock(curatorFramework);
    }

    @Bean
    public CounterLock counterLock(CuratorFramework curatorFramework) {
        return new CounterLock(curatorFramework);
    }

}
