package com.yj2025.weixin.work.impl.memory;

import com.yj2025.weixin.work.WorkWeixinProperties;
import com.yj2025.weixin.work.impl.AbstractBaseTenantOperator;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public class MemoryTenantOperator extends AbstractBaseTenantOperator {

    protected final Map<String, String> configRuntimeKeyValues;
    protected final Map<String, Integer> configRuntimeKeyExpireds;
    private final Timer timer;
    protected transient Lock accessTokenLock = new ReentrantLock();
    protected transient Lock jsapiTicketLock = new ReentrantLock();
    protected transient Lock agentJsapiTicketLock = new ReentrantLock();

    public MemoryTenantOperator(WorkWeixinProperties properties) {
        super(properties);
        this.configRuntimeKeyValues = new ConcurrentHashMap<>();
        this.configRuntimeKeyExpireds = new ConcurrentHashMap<>();
        this.timer = new Timer("memory_expireds_checker", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndUpdateExpiredKeys();
            }
        }, 0, 1000);
    }


    @Override
    protected String get(String key) {
        return configRuntimeKeyValues.get(key);
    }

    @Override
    protected void set(String key, String value) {
        Assert.notNull(key, "key不能为空");
        if (value == null) {
            return;
        }
        configRuntimeKeyValues.put(key, value);
    }

    @Override
    protected void set(String key, String value, int expiredSeconds) {
        Assert.notNull(expiredSeconds, "超时时间不能为空");
        set(key, value);
        if (expiredSeconds > 0) {
            configRuntimeKeyExpireds.put(key, expiredSeconds);
        }
    }

    @Override
    protected void remove(String key) {
        configRuntimeKeyValues.remove(key);
        configRuntimeKeyExpireds.remove(key);
    }

    @Override
    protected boolean exist(String key) {
        return configRuntimeKeyValues.containsKey(key);
    }

    @Override
    protected long getExpiredSeconds(String key) {
        Integer integer = configRuntimeKeyExpireds.get(key);
        if (integer == null) {
            integer = 0;
        }
        return Long.valueOf(integer.toString());
    }

    private void checkAndUpdateExpiredKeys() {
        for (String key : configRuntimeKeyExpireds.keySet()) {
            Integer integer = configRuntimeKeyExpireds.get(key);
            if (integer == null) {
                return;
            }
            integer--;
//            System.out.println("还剩: " + integer + "到期");
            if (integer == 0) {
                configRuntimeKeyExpireds.remove(key);
                configRuntimeKeyValues.remove(key);
            } else {
                configRuntimeKeyExpireds.put(key, integer);
            }
        }
    }


    @Override
    public Lock getAccessTokenLock(String tenantId) {
        return accessTokenLock;
    }

    @Override
    public Lock getJsapiTicketLock(String tenantId) {
        return jsapiTicketLock;
    }


    @Override
    public Lock getAgentJsapiTicketLock(String tenantId) {
        return agentJsapiTicketLock;
    }

}
