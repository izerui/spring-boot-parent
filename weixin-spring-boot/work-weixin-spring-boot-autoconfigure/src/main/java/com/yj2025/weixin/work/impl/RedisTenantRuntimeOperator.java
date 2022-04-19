package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.TenantRuntimeOperator;
import com.yj2025.weixin.work.WorkWeixinProperties;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.locks.RedisTemplateSimpleDistributedLock;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public class RedisTenantRuntimeOperator implements TenantRuntimeOperator, KeyConstants {

    private StringRedisTemplate redisTemplate;
    private WorkWeixinProperties properties;

    public RedisTenantRuntimeOperator(StringRedisTemplate redisTemplate, WorkWeixinProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public Lock getAccessTokenLock(String tenantId) {
        return new RedisTemplateSimpleDistributedLock(redisTemplate, TOKEN_KEY.apply(tenantId).concat("_lock"), 60);
    }

    @Override
    public boolean isAccessTokenExpired(String tenantId) {
        Boolean hasKey = redisTemplate.hasKey(TOKEN_KEY.apply(tenantId));
        return !hasKey;
    }

    @Override
    public void expireAccessToken(String tenantId) {
        redisTemplate.delete(ACCESSTOKEN_KEY.apply(tenantId));
    }

    @Override
    public Lock getJsapiTicketLock(String tenantId) {
        return new RedisTemplateSimpleDistributedLock(redisTemplate, JSAPITICKET_KEY.apply(tenantId).concat("_lock"), 60);
    }

    @Override
    public boolean isJsapiTicketExpired(String tenantId) {
        Boolean hasKey = redisTemplate.hasKey(JSAPITICKET_KEY.apply(tenantId));
        return !hasKey;
    }

    @Override
    public void expireJsapiTicket(String tenantId) {
        redisTemplate.delete(JSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public String getJsapiTicket(String tenantId) {
        return redisTemplate.boundValueOps(JSAPITICKET_KEY.apply(tenantId)).get();
    }

    @Override
    public void setJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds) {
        redisTemplate.boundValueOps(JSAPITICKET_KEY.apply(tenantId)).set(jsapiTicket, expiresInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getAgentJsapiTicket(String tenantId) {
        return redisTemplate.boundValueOps(AGENTJSAPITICKET_KEY.apply(tenantId)).get();
    }

    @Override
    public Lock getAgentJsapiTicketLock(String tenantId) {
        return new RedisTemplateSimpleDistributedLock(redisTemplate, AGENTJSAPITICKET_KEY.apply(tenantId).concat("_lock"), 60);
    }

    @Override
    public boolean isAgentJsapiTicketExpired(String tenantId) {
        Boolean hasKey = redisTemplate.hasKey(AGENTJSAPITICKET_KEY.apply(tenantId));
        return !hasKey;
    }

    @Override
    public void expireAgentJsapiTicket(String tenantId) {
        redisTemplate.delete(AGENTJSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void updateAgentJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds) {
        redisTemplate.boundValueOps(AGENTJSAPITICKET_KEY.apply(tenantId)).set(jsapiTicket, expiresInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void updateAccessToken(String tenantId, WxAccessToken accessToken) {
        redisTemplate.boundValueOps(ACCESSTOKEN_KEY.apply(tenantId)).set(accessToken.getAccessToken(), accessToken.getExpiresIn(), TimeUnit.SECONDS);
    }

    @Override
    public String getAccessToken(String tenantId) {
        return redisTemplate.boundValueOps(ACCESSTOKEN_KEY.apply(tenantId)).get();
    }

    @Override
    public long getExpiresTime(String tenantId) {
        return redisTemplate.boundValueOps(ACCESSTOKEN_KEY.apply(tenantId)).getExpire();
    }
}
