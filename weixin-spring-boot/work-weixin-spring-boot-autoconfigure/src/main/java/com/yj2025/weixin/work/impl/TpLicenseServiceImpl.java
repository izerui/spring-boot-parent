package com.yj2025.weixin.work.impl;

import com.google.gson.JsonObject;
import com.yj2025.weixin.work.TpService;
import com.yj2025.weixin.work.WxProperties;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;

public class TpLicenseServiceImpl implements TpService.TpLicenseService {

    private TpService tpService;
    private WxProperties properties;

    public TpLicenseServiceImpl(TpService tpService, WxProperties properties) {
        this.tpService = tpService;
        this.properties = properties;
    }

    @Override
    public TpService getTpService() {
        return tpService;
    }

    @Override
    public String createOrder(String tenantId, Integer accountNum, boolean external, Integer months) throws WxErrorException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("corpid", tpService.getConfigOperator().getCorpId(tenantId));
        jsonObject.addProperty("buyer_userid", properties.getTpConfig().getBuyUserId());
        JsonObject accountCountObj = new JsonObject();
        if (external) {
            accountCountObj.addProperty("external_contact_count", accountNum);
        } else {
            accountCountObj.addProperty("base_count", accountNum);
        }
        jsonObject.add("account_count", accountCountObj);

        JsonObject monthObj = new JsonObject();
        monthObj.addProperty("months", months);
        jsonObject.add("account_duration", monthObj);

        String access_token = tpService.getWxCpProviderToken();
        String respJson = tpService.post(tpService.getWxCpTpConfigStorage().getApiUrl("/cgi-bin/license/create_new_order") + "?provider_access_token=" + access_token, jsonObject.toString(), true);
        return WxGsonBuilder.create().fromJson(respJson, JsonObject.class).get("order_id").getAsString();

    }

    /**
     * 激活账号
     *
     * @param tenantId
     * @param activeCode
     * @param authUserId
     * @return
     * @throws WxErrorException
     */
    @Override
    public void activeAccount(String tenantId, String activeCode, String authUserId) throws WxErrorException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("active_code", activeCode);
        jsonObject.addProperty("corpid", tpService.getConfigOperator().getCorpId(tenantId));
        jsonObject.addProperty("userid", authUserId);
        String access_token = tpService.getWxCpProviderToken();
        tpService.post(tpService.getWxCpTpConfigStorage().getApiUrl("/cgi-bin/license/active_account") + "?provider_access_token=" + access_token, jsonObject.toString(), true);
    }
}
