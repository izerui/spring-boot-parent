package com.yj2025.basic.web.request;

import com.yj2025.basic.web.support.WebRequestAware;

import java.io.Serializable;

public abstract class BasicAuthReq implements Serializable {

    private Auth auth;

    public Auth getAuth() {
        return auth;
    }

    public static class Auth implements WebRequestAware {
    }
}
