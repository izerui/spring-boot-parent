package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.TpService;
import me.chanjar.weixin.cp.tp.service.*;
import me.chanjar.weixin.cp.tp.service.impl.WxCpTpServiceImpl;

public class TpServiceImpl extends WxCpTpServiceImpl implements TpService {

    @Deprecated
    @Override
    public WxCpTpContactService getWxCpTpContactService() {
        throw new UnsupportedOperationException("不建议用，wxjava-sdk封装不完善");
    }

    @Deprecated
    @Override
    public WxCpTpDepartmentService getWxCpTpDepartmentService() {
        throw new UnsupportedOperationException("不建议用，wxjava-sdk封装不完善");
    }

    @Deprecated
    @Override
    public WxCpTpMediaService getWxCpTpMediaService() {
        throw new UnsupportedOperationException("不建议用，wxjava-sdk封装不完善");
    }

    @Deprecated
    @Override
    public WxCpTpOAService getWxCpTpOAService() {
        throw new UnsupportedOperationException("不建议用，wxjava-sdk封装不完善");
    }

    @Deprecated
    @Override
    public WxCpTpUserService getWxCpTpUserService() {
        throw new UnsupportedOperationException("不建议用，wxjava-sdk封装不完善");
    }
}
