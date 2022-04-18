package com.yj2025.weixin.work;

import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.locks.RedisTemplateSimpleDistributedLock;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 租户运行时暂存结果操作类(继承于配置类)
 *
 * @author liuyuhua
 * @date 2022/4/18
 */
@ThreadSafe
public interface WorkWeixinTenantRuntimeOperator {

    Lock getAccessTokenLock(String tenantId);

    boolean isAccessTokenExpired(String tenantId);

    void expireAccessToken(String tenantId);

    Lock getJsapiTicketLock(String tenantId);

    boolean isJsapiTicketExpired(String tenantId);

    void expireJsapiTicket(String tenantId);

    String getJsapiTicket(String tenantId);

    void setJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds);

    String getAgentJsapiTicket(String tenantId);

    Lock getAgentJsapiTicketLock(String tenantId);

    boolean isAgentJsapiTicketExpired(String tenantId);

    void expireAgentJsapiTicket(String tenantId);

    void updateAgentJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds);

    void updateAccessToken(String tenantId, WxAccessToken accessToken);

    String getAccessToken(String tenantId);

    long getExpiresTime(String tenantId);

    class Default implements WorkWeixinTenantRuntimeOperator, IWorkWeixinConstant {

        private StringRedisTemplate redisTemplate;

        public Default(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        @Override
        public Lock getAccessTokenLock(String tenantId) {
            return new RedisTemplateSimpleDistributedLock(redisTemplate, $_(TOKEN_KEY, tenantId).concat("_lock"), 60);
        }

        @Override
        public boolean isAccessTokenExpired(String tenantId) {
            Boolean hasKey = redisTemplate.hasKey($_(TOKEN_KEY, tenantId));
            return !hasKey;
        }

        @Override
        public void expireAccessToken(String tenantId) {
            redisTemplate.delete($_(TOKEN_KEY, tenantId));
        }

        @Override
        public Lock getJsapiTicketLock(String tenantId) {
            return new RedisTemplateSimpleDistributedLock(redisTemplate, $_(JSAPITICKET_KEY, tenantId).concat("_lock"), 60);
        }

        @Override
        public boolean isJsapiTicketExpired(String tenantId) {
            Boolean hasKey = redisTemplate.hasKey($_(JSAPITICKET_KEY, tenantId));
            return !hasKey;
        }

        @Override
        public void expireJsapiTicket(String tenantId) {
            redisTemplate.delete($_(JSAPITICKET_KEY, tenantId));
        }

        @Override
        public String getJsapiTicket(String tenantId) {
            return redisTemplate.boundValueOps($_(JSAPITICKET_KEY, tenantId)).get();
        }

        @Override
        public void setJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds) {
            redisTemplate.boundValueOps($_(JSAPITICKET_KEY, tenantId)).set(jsapiTicket, expiresInSeconds, TimeUnit.SECONDS);
        }

        @Override
        public String getAgentJsapiTicket(String tenantId) {
            return redisTemplate.boundValueOps($_(AGENTJSAPITICKET_KEY, tenantId)).get();
        }

        @Override
        public Lock getAgentJsapiTicketLock(String tenantId) {
            return new RedisTemplateSimpleDistributedLock(redisTemplate, $_(AGENTJSAPITICKET_KEY, tenantId).concat("_lock"), 60);
        }

        @Override
        public boolean isAgentJsapiTicketExpired(String tenantId) {
            Boolean hasKey = redisTemplate.hasKey($_(AGENTJSAPITICKET_KEY, tenantId));
            return !hasKey;
        }

        @Override
        public void expireAgentJsapiTicket(String tenantId) {
            redisTemplate.delete($_(AGENTJSAPITICKET_KEY, tenantId));
        }

        @Override
        public void updateAgentJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds) {
            redisTemplate.boundValueOps($_(AGENTJSAPITICKET_KEY, tenantId)).set(jsapiTicket, expiresInSeconds, TimeUnit.SECONDS);
        }

        @Override
        public void updateAccessToken(String tenantId, WxAccessToken accessToken) {
            redisTemplate.boundValueOps($_(ACCESSTOKEN_KEY, tenantId)).set(accessToken.getAccessToken(), accessToken.getExpiresIn(), TimeUnit.SECONDS);
        }

        @Override
        public String getAccessToken(String tenantId) {
            return redisTemplate.boundValueOps($_(ACCESSTOKEN_KEY, tenantId)).get();
        }

        @Override
        public long getExpiresTime(String tenantId) {
            return redisTemplate.boundValueOps($_(ACCESSTOKEN_KEY, tenantId)).getExpire();
        }

    }

}
