package com.yj2025.weixin.work;

import me.chanjar.weixin.cp.tp.service.*;

/**
 * 没别的用，就是不让你用这些方法，太垃圾了
 */
public interface TpService extends WxCpTpService {

    @Deprecated
    @Override
    WxCpTpContactService getWxCpTpContactService();

    @Deprecated
    @Override
    WxCpTpDepartmentService getWxCpTpDepartmentService();

    @Deprecated
    @Override
    WxCpTpMediaService getWxCpTpMediaService();

    @Deprecated
    @Override
    WxCpTpOAService getWxCpTpOAService();

    @Deprecated
    @Override
    WxCpTpUserService getWxCpTpUserService();
}
