package com.yj2025.weixin.work.config.impl.redis;

import com.yj2025.weixin.work.WorkWeixinProperties;
import com.yj2025.weixin.work.config.impl.AbstractBaseTenantOperator;
import me.chanjar.weixin.common.util.locks.RedisTemplateSimpleDistributedLock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public class RedisTenantOperator extends AbstractBaseTenantOperator {

    protected StringRedisTemplate redisTemplate;

    public RedisTenantOperator(WorkWeixinProperties properties, StringRedisTemplate redisTemplate) {
        super(properties);
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected String get(String key) {
        return redisTemplate.boundValueOps(key).get();
    }

    @Override
    protected void set(String key, String value) {
        Assert.notNull(key, "key不能为空");
        if (value == null) {
            return;
        }
        redisTemplate.boundValueOps(key).set(value);
    }

    @Override
    protected void set(String key, String value, int expiredSeconds) {
        Assert.notNull(expiredSeconds, "超时时间不能为空");
        if (expiredSeconds > 0) {
            redisTemplate.boundValueOps(key).set(value, expiredSeconds, TimeUnit.SECONDS);
            return;
        }
        throw new RuntimeException("超时时间必须大于0");
    }

    @Override
    protected void remove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    protected boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    protected long getExpiredSeconds(String key) {
        return redisTemplate.boundValueOps(key).getExpire();
    }

    @Override
    public Lock getAccessTokenLock(String tenantId) {
        return new RedisTemplateSimpleDistributedLock(redisTemplate, TOKEN_KEY.apply(tenantId).concat("_lock"), 60);
    }

    @Override
    public Lock getJsapiTicketLock(String tenantId) {
        return new RedisTemplateSimpleDistributedLock(redisTemplate, JSAPITICKET_KEY.apply(tenantId).concat("_lock"), 60);
    }

    @Override
    public Lock getAgentJsapiTicketLock(String tenantId) {
        return new RedisTemplateSimpleDistributedLock(redisTemplate, AGENTJSAPITICKET_KEY.apply(tenantId).concat("_lock"), 60);
    }

}
