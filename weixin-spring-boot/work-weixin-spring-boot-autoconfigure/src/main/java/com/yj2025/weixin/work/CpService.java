package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.ConfigOperator;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface CpService extends WxCpService {

    /**
     * 指定以某一个租户操作service请求
     *
     * @param tenantId   租户ID
     * @param isThirdApp 是否是第三方应用
     * @return
     */
    CpService tenant(String tenantId, boolean isThirdApp);

    /**
     * 获取配置操作对象
     *
     * @return
     */
    ConfigOperator getConfigOperator();

    /**
     * 获取桥接的存储适配器
     *
     * @return
     */
    ConfigStorageAdpatder getStorageAdpatder();

    /**
     * 获取第三方应用service
     * @return
     */
    WxCpTpService getTpService();
}
