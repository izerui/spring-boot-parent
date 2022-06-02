package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.adpatder.TenantWxCpConfigStorageAdpatder;
import com.yj2025.weixin.work.config.TenantWxCpConfigOperator;
import me.chanjar.weixin.cp.api.WxCpService;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface TenantWxCpService extends WxCpService {

    /**
     * 获取当前tenantId
     * @return
     */
    String getTenantId();
    /**
     * 指定以某一个租户操作service请求
     *
     * @param tenantId
     * @return
     */
    TenantWxCpService tenant(String tenantId);

    /**
     * 获取配置操作对象
     *
     * @return
     */
    TenantWxCpConfigOperator getTenantOperator();

    /**
     * 获取桥接的存储适配器
     *
     * @return
     */
    TenantWxCpConfigStorageAdpatder getStorageAdpatder();
}
