package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.TenantRuntimeOperator;
import com.yj2025.weixin.work.WorkWeixinProperties;
import me.chanjar.weixin.common.bean.WxAccessToken;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public class MemoryTenantRuntimeOperator extends BaseMemoryTenantOperator implements TenantRuntimeOperator {

    protected transient Lock accessTokenLock = new ReentrantLock();
    protected transient Lock jsapiTicketLock = new ReentrantLock();
    protected transient Lock agentJsapiTicketLock = new ReentrantLock();

    public MemoryTenantRuntimeOperator(WorkWeixinProperties properties) {
        super(properties);
    }

    @Override
    public Lock getAccessTokenLock(String tenantId) {
        return accessTokenLock;
    }

    @Override
    public boolean isAccessTokenExpired(String tenantId) {
        return !exist(ACCESSTOKEN_KEY.apply(tenantId));
    }

    @Override
    public void expireAccessToken(String tenantId) {
        remove(ACCESSTOKEN_KEY.apply(tenantId));
    }

    @Override
    public Lock getJsapiTicketLock(String tenantId) {
        return jsapiTicketLock;
    }

    @Override
    public boolean isJsapiTicketExpired(String tenantId) {
        return !exist(JSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void expireJsapiTicket(String tenantId) {
        remove(JSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public String getJsapiTicket(String tenantId) {
        return get(JSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void setJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds) {
        set(JSAPITICKET_KEY.apply(tenantId), jsapiTicket, expiresInSeconds);
    }

    @Override
    public String getAgentJsapiTicket(String tenantId) {
        return get(AGENTJSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public Lock getAgentJsapiTicketLock(String tenantId) {
        return agentJsapiTicketLock;
    }

    @Override
    public boolean isAgentJsapiTicketExpired(String tenantId) {
        return !exist(AGENTJSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void expireAgentJsapiTicket(String tenantId) {
        remove(AGENTJSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void updateAgentJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds) {
        set(AGENTJSAPITICKET_KEY.apply(tenantId), jsapiTicket, expiresInSeconds);
    }

    @Override
    public void updateAccessToken(String tenantId, WxAccessToken accessToken) {
        set(ACCESSTOKEN_KEY.apply(tenantId), accessToken.getAccessToken(), accessToken.getExpiresIn());
    }

    @Override
    public String getAccessToken(String tenantId) {
        return get(ACCESSTOKEN_KEY.apply(tenantId));
    }

    @Override
    public long getExpiresTime(String tenantId) {
        return getExpiredSeconds(ACCESSTOKEN_KEY.apply(tenantId));
    }
}
