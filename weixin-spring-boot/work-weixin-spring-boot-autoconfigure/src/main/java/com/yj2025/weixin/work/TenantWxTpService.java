package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.TenantWxTpConfigStorageOperator;
import com.yj2025.weixin.work.config.adpatder.TenantWxTpConfigStorageAdpatder;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;

public interface TenantWxTpService extends WxCpTpService {

    /**
     * 获取当前tenantId
     *
     * @return
     */
    String tenantId();

    /**
     * 指定以某一个租户操作service请求
     *
     * @param tenantId
     * @return
     */
    TenantWxTpService tenant(String tenantId);

    /**
     * 获取配置操作对象
     *
     * @return
     */
    TenantWxTpConfigStorageOperator getTenantOperator();

    /**
     * 获取桥接的存储适配器
     *
     * @return
     */
    TenantWxTpConfigStorageAdpatder getStorageAdpatder();
}
