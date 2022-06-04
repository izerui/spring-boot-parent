package com.yj2025.weixin.work.impl;

import com.google.gson.JsonObject;
import com.yj2025.weixin.work.TpService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxTpLoginInfo;
import me.chanjar.weixin.cp.tp.service.*;
import me.chanjar.weixin.cp.tp.service.impl.WxCpTpServiceImpl;

import static me.chanjar.weixin.cp.constant.WxCpApiPathConsts.Tp.GET_LOGIN_INFO;

public class TpServiceImpl extends WxCpTpServiceImpl implements TpService {

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
}
