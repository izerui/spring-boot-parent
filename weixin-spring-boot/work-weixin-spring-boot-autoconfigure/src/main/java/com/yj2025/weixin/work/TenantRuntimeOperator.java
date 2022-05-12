package com.yj2025.weixin.work;

import me.chanjar.weixin.common.bean.WxAccessToken;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.locks.Lock;

/**
 * 租户运行时暂存结果操作类
 *
 * @author liuyuhua
 * @date 2022/4/18
 */
@ThreadSafe
public interface TenantRuntimeOperator {

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

}
