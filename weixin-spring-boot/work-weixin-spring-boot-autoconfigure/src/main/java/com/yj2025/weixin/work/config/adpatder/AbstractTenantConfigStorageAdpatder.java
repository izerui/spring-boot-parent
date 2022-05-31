package com.yj2025.weixin.work.config.adpatder;

import com.yj2025.weixin.work.WorkWeixinProperties;
import com.yj2025.weixin.work.config.BaseTenantConfigStorageOperator;
import lombok.Getter;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.constant.WxCpApiPathConsts;
import org.springframework.beans.factory.ObjectProvider;

public abstract class AbstractTenantConfigStorageAdpatder<T extends BaseTenantConfigStorageOperator> {

    // 当未指定租户ID的时候使用的默认的租户ID
    private final static String DEFAULT_TENANT_ID = "default";

    // 当前线程使用的tenantId(公司编号)
    protected final static InheritableThreadLocal<String> INHERITABLE_THREAD_ACTIVE_TENANT_ID;

    static {
        INHERITABLE_THREAD_ACTIVE_TENANT_ID = new InheritableThreadLocal<>();
        INHERITABLE_THREAD_ACTIVE_TENANT_ID.set(DEFAULT_TENANT_ID);
    }

    @Getter
    protected T tenantOperator;
    @Getter
    protected WorkWeixinProperties properties;
    protected ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders;

    public AbstractTenantConfigStorageAdpatder(T tenantOperator,
                                               WorkWeixinProperties properties,
                                               ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders) {
        this.tenantOperator = tenantOperator;
        this.properties = properties;
        this.apacheHttpClientBuilders = apacheHttpClientBuilders;
    }

    /**
     * 当前请求线程切换使用的租户配置
     *
     * @param tenantId
     * @return
     */
    public <T extends AbstractTenantConfigStorageAdpatder> T tenant(String tenantId) {
        INHERITABLE_THREAD_ACTIVE_TENANT_ID.set(tenantId);
        return (T) this;
    }

    public String tenantId() {
        return INHERITABLE_THREAD_ACTIVE_TENANT_ID.get();
    }

    @Deprecated
    public void setBaseApiUrl(String baseUrl) {
        throw new UnsupportedOperationException();
    }

    public String getApiUrl(String path) {
        return WxCpApiPathConsts.DEFAULT_CP_BASE_URL + path;
    }
}
