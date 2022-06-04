package com.yj2025.weixin.work;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.tp.service.*;

/**
 * 没别的用，就是不让你用这些方法，太垃圾了
 */
public interface TpService extends WxCpTpService {

    String activeAccount(String activeCode, String authCorpId, String authUserId) throws WxErrorException;
}
