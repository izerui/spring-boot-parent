package com.yj2025.weixin.work22;

import com.yj2025.weixin.work.WxProperties;
import com.yj2025.weixin.work.provider.TpAuthConfigLoader;
import org.springframework.stereotype.Component;

@Component
public class TpAuthAppLoader implements TpAuthConfigLoader {
    @Override
    public WxProperties.TpAuthConfig getConfig(String tenantId) {
        return new WxProperties.TpAuthConfig()
                .setTenantId("yunji-wode")
                .setCorpId("ww7c4f40dafaee2f4c")
                .setPermanentCode("Axkfn5p6oE-Z0WiKY5CIQw2l3ZZZVmfAVnbtItorUWs");
    }
}
