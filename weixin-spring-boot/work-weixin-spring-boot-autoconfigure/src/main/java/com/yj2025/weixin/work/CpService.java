package com.yj2025.weixin.work;

import com.yj2025.weixin.work.config.CpConfigStorageAdpatder;
import com.yj2025.weixin.work.config.CpConfigOperator;
import me.chanjar.weixin.cp.api.WxCpService;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface CpService extends WxCpService {

    /**
     * 指定以某一个租户操作service请求
     *
     * @param tenantId
     * @return
     */
    CpService tenant(String tenantId);

    /**
     * 获取配置操作对象
     *
     * @return
     */
    CpConfigOperator getTenantOperator();

    /**
     * 获取桥接的存储适配器
     *
     * @return
     */
    CpConfigStorageAdpatder getStorageAdpatder();
}
