package com.yj2025.weixin.work.config.redis;

import com.yj2025.weixin.work.WxProperties;
import com.yj2025.weixin.work.config.AbstractCpConfigOperator;
import me.chanjar.weixin.common.util.locks.RedisTemplateSimpleDistributedLock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.yj2025.weixin.work.config.KeyConstants.*;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public class RedisCpConfigOperator extends AbstractCpConfigOperator {

    protected StringRedisTemplate redisTemplate;

    public RedisCpConfigOperator(WxProperties properties,
                                 StringRedisTemplate redisTemplate) {
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


    // config
    @Override
    public void setConfigs(WxProperties.CpConfig... configs) {
        for (WxProperties.CpConfig config : configs) {
            this.setCorpId(config.getTenantId(), config.getCorpId());
            this.setCorpSecret(config.getTenantId(), config.getCorpSecret());
            this.setToken(config.getTenantId(), config.getListenerToken());
            this.setAesKey(config.getTenantId(), config.getListenerAesKey());
            this.setAgentId(config.getTenantId(), config.getAgentId());
            this.setMsgAuditLibPath(config.getTenantId(), config.getMsgAuditLibPath());
            this.setWebhookKey(config.getTenantId(), config.getWebhookKey());
            this.setOauth2redirectUri(config.getTenantId(), config.getOauth2redirectUri());
        }
    }

    @Override
    public WxProperties.CpConfig getConfig(String tenantId) {
        return new WxProperties.CpConfig()
                .setTenantId(tenantId)
                .setCorpId(getCorpId(tenantId))
                .setCorpSecret(getCorpSecret(tenantId))
                .setListenerToken(getToken(tenantId))
                .setListenerAesKey(getAesKey(tenantId))
                .setAgentId(getAgentId(tenantId))
                .setMsgAuditLibPath(getMsgAuditLibPath(tenantId))
                .setWebhookKey(getWebhookKey(tenantId));
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
