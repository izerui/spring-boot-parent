package com.yj2025.basic.web.support;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface AuthAware {

    @JsonIgnore
    default String getEntCode() {
        return RequestHeaderHolder.getEntCode();
    }

    @JsonIgnore
    default String getEntName() {
        return RequestHeaderHolder.getEntName();
    }

    @JsonIgnore
    default String getUserCode() {
        return RequestHeaderHolder.getUserCode();
    }

    @JsonIgnore
    default String getUserName() {
        return RequestHeaderHolder.getUserName();
    }

    default String getAccountCode() {
        return RequestHeaderHolder.getAccountCode();
    }

    default String getAccountName() {
        return RequestHeaderHolder.getAccountName();
    }

    @JsonIgnore
    default String getPostCode() {
        return RequestHeaderHolder.getPostCode();
    }

}
