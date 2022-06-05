package com.yj2025.weixin.work;

import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 服务商license服务
 */
public interface TpLicenseService {

    /**
     * 获取第三方服务商服务对象
     *
     * @return
     */
    TpService getTpService();

    /**
     * 下单购买帐号
     *
     * @param tenantId   租户ID
     * @param accountNum 购买账号数量
     * @param external   是否互通账号，默认基础账号
     * @param months     月份数量
     * @return 订单号
     */
    String createOrder(String tenantId, Integer accountNum, boolean external, Integer months) throws WxErrorException;


    /**
     * https://developer.work.weixin.qq.com/document/path/95553
     * 激活账号，以进一步可使第三方应用拥有接口的调用许可
     *
     * @param tenantId
     * @param activeCode
     * @param authUserId
     * @return
     * @throws WxErrorException
     */
    void activeAccount(String tenantId, String activeCode, String authUserId) throws WxErrorException;

}