package com.yj2025.basic.web.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yj2025.basic.support.ApplicationBeanAware;
import com.yj2025.basic.web.support.AuthAware;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseQueryRequestVO implements AuthAware, ApplicationBeanAware {

    @JsonIgnore
    public Map<String, Object> getQueryMap() {
        return new LinkedHashMap<>();
    }
}
