package com.yj2025.weixin.work22.ureport;

import com.yj2025.weixin.work.CpService;
import com.yj2025.weixin.work.WxProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ConfigDataSource {

    @Autowired
    private CpService cpService;


    public List<WxProperties.CpConfig> getConfigDataSource(String dataSourceName, String dataSetName, Map<String, Object> params) {
        return cpService.getConfigs();
    }

}
