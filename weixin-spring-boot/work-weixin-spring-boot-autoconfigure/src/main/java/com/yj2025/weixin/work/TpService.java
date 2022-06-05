package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.ConfigOperator;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.tp.service.*;

/**
 * 没别的用，就是不让你用这些方法，太垃圾了
 */
public interface TpService extends WxCpTpService {

    /**
     * 获取企业微信配置存储对象
     *
     * @return
     */
    WxCpTpConfigStorage getWxCpTpConfigStorage();

    /**
     * 对应的配置操作对象
     *
     * @return
     */
    ConfigOperator getConfigOperator();

    /**
     * @return
     */
    TpLicenseService getLicenseService();


    /**
     * 获取指定租户的调用对象
     *
     * @param tenantId
     * @return
     */
    CpService getCpService(String tenantId);

    /**
     * 根据tenantId获取授权企业的配置
     *
     * @param tenantId
     * @return
     */
    WxProperties.TpAuthConfig getAuthConfig(String tenantId);

    /**
     * 请使用 {@link #getCpService(String)}} 获取其相应的service操作对象
     *
     * @return
     */
    @Deprecated
    @Override
    WxCpTpUserService getWxCpTpUserService();

    /**
     * 请使用 {@link #getCpService(String)}} 获取其相应的service操作对象
     *
     * @return
     */
    @Deprecated
    @Override
    WxCpTpDepartmentService getWxCpTpDepartmentService();

    /**
     * 请使用 {@link #getCpService(String)}} 获取其相应的service操作对象
     *
     * @return
     */
    @Deprecated
    @Override
    WxCpTpOAService getWxCpTpOAService();

    /**
     * 请使用 {@link #getCpService(String)}} 获取其相应的service操作对象
     *
     * @return
     */
    @Deprecated
    @Override
    WxCpTpMediaService getWxCpTpMediaService();

    /**
     * 暴露 {@link #post(String, String, boolean)}
     *
     * @param url
     * @param postData
     * @param withoutSuiteAccessToken
     * @return
     * @throws WxErrorException
     */
    String post(String url, String postData, boolean withoutSuiteAccessToken) throws WxErrorException;

    /**
     * 服务商license服务
     */
    interface TpLicenseService {

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
}
