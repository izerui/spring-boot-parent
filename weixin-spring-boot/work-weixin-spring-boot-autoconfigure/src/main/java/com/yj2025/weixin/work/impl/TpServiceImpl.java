package com.yj2025.weixin.work.impl;

import com.google.gson.JsonObject;
import com.yj2025.weixin.work.CpService;
import com.yj2025.weixin.work.TpService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.tp.service.WxCpTpDepartmentService;
import me.chanjar.weixin.cp.tp.service.WxCpTpMediaService;
import me.chanjar.weixin.cp.tp.service.WxCpTpOAService;
import me.chanjar.weixin.cp.tp.service.WxCpTpUserService;
import me.chanjar.weixin.cp.tp.service.impl.WxCpTpServiceImpl;

public class TpServiceImpl extends WxCpTpServiceImpl implements TpService {

    private CpService cpService;

    public void setCpService(CpService cpService) {
        this.cpService = cpService;
    }

    /**
     * 激活账号
     *
     * @param activeCode
     * @param authCorpId
     * @param authUserId
     * @return
     * @throws WxErrorException
     */
    @Override
    public String activeAccount(String activeCode, String authCorpId, String authUserId) throws WxErrorException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("active_code", activeCode);
        jsonObject.addProperty("corpid", authCorpId);
        jsonObject.addProperty("userid", authUserId);
        String access_token = getWxCpProviderToken();
        String responseText = post(configStorage.getApiUrl("/cgi-bin/license/active_account") + "?provider_access_token=" + access_token, jsonObject.toString(), true);
        return responseText;
    }

    @Override
    public CpService getCpService(String tenantId) {
        return cpService.tenant(tenantId, true);
    }

    @Override
    public WxCpTpUserService getWxCpTpUserService() {
        throw new UnsupportedOperationException("不支持当前操作");
    }

    @Override
    public WxCpTpDepartmentService getWxCpTpDepartmentService() {
        throw new UnsupportedOperationException("不支持当前操作");
    }

    @Override
    public WxCpTpOAService getWxCpTpOAService() {
        throw new UnsupportedOperationException("不支持当前操作");
    }

    @Override
    public WxCpTpMediaService getWxCpTpMediaService() {
        throw new UnsupportedOperationException("不支持当前操作");
    }

}
