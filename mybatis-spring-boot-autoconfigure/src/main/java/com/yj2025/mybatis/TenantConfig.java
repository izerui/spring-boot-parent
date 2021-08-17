package com.yj2025.mybatis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "mybatis.tenant")
public class TenantConfig implements Serializable {

    //默认开启租户模式
    private boolean enable = true;

    //租户模式下统一的字段名
    private String field = "ent_code";

    //开启租户模式后,需要忽略的表名
    private List<String> ignores = new ArrayList<String>();

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<String> getIgnores() {
        if (ignores == null) {
            ignores = new ArrayList<>();
        }
        return ignores;
    }

    public void setIgnores(List<String> ignores) {
        this.ignores = ignores;
    }
}
