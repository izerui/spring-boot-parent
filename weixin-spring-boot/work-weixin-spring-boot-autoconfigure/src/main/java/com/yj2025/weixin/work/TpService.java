package com.yj2025.weixin.work;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.tp.service.*;

/**
 * 没别的用，就是不让你用这些方法，太垃圾了
 */
public interface TpService extends WxCpTpService {

    /**
     * 获取企业微信配置存储对象
     * @return
     */
    WxCpTpConfigStorage getWxCpTpConfigStorage();

    /**
     * https://developer.work.weixin.qq.com/document/path/95553
     * 激活账号，以进一步可使第三方应用拥有接口的调用许可
     *
     * @param activeCode
     * @param authCorpId
     * @param authUserId
     * @return
     * @throws WxErrorException
     */
    String activeAccount(String activeCode, String authCorpId, String authUserId) throws WxErrorException;


    /**
     * 获取指定租户的调用对象
     *
     * @param tenantId
     * @return
     */
    CpService getCpService(String tenantId);

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

}
