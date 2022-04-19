package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.WorkWeixinProperties;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public abstract class BaseMemoryTenantOperator implements KeyConstants {

    protected WorkWeixinProperties properties;
    protected final Map<String, String> configRuntimeKeyValues;
    protected final Map<String, Integer> configRuntimeKeyExpireds;
    private final Timer timer;

    public BaseMemoryTenantOperator(WorkWeixinProperties properties) {
        this.properties = properties;
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

    protected String get(String key) {
        return configRuntimeKeyValues.get(key);
    }

    protected void set(String key, String value) {
        Assert.notNull(key);
        Assert.notNull(value);
        configRuntimeKeyValues.put(key, value);
    }

    protected void set(String key, String value, Integer expiredSeconds) {
        Assert.notNull(key);
        Assert.notNull(value);
        Assert.notNull(expiredSeconds, "超时时间不能为空");
        set(key, value);
        if (expiredSeconds > 0) {
            configRuntimeKeyExpireds.put(key, expiredSeconds);
        }
    }

    protected void remove(String key) {
        configRuntimeKeyValues.remove(key);
        configRuntimeKeyExpireds.remove(key);
    }

    protected boolean exist(String key) {
        return configRuntimeKeyValues.containsKey(key);
    }

    protected int getExpiredSeconds(String key) {
        Integer integer = configRuntimeKeyExpireds.get(key);
        if (integer == null) {
            integer = 0;
        }
        return integer;
    }

    protected String searchKeyByValue(String keyPattern, String value) {
        for (String key : configRuntimeKeyValues.keySet()) {
            if (key.startsWith(keyPattern)
                    && configRuntimeKeyValues.get(key).equals(value)) {
                return key;
            }
        }
        return null;
    }

    private void checkAndUpdateExpiredKeys() {
        for (String key : configRuntimeKeyExpireds.keySet()) {
            Integer integer = configRuntimeKeyExpireds.get(key);
            integer--;
            if (integer != null && integer == 0) {
                configRuntimeKeyExpireds.remove(key);
                configRuntimeKeyValues.remove(key);
            } else {
                configRuntimeKeyExpireds.put(key, integer);
            }
        }
    }

}
