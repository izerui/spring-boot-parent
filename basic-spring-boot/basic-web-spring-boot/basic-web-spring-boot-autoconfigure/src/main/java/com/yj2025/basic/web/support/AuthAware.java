package com.yj2025.basic.web.support;

public interface AuthAware {

    default String getEntCode() {
        return RequestHeaderHolder.getEntCode();
    }

    default String getEntName() {
        return RequestHeaderHolder.getEntName();
    }

    default String getUserCode() {
        return RequestHeaderHolder.getUserCode();
    }

    default String getUserName() {
        return RequestHeaderHolder.getUserName();
    }

    default String getAccountCode() {
        return RequestHeaderHolder.getAccountCode();
    }

    default String getAccountName() {
        return RequestHeaderHolder.getAccountName();
    }

    default String getPostCode() {
        return RequestHeaderHolder.getPostCode();
    }

}
