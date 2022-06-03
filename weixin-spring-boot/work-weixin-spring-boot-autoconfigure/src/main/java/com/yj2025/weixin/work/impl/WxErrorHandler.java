package com.yj2025.weixin.work.impl;

import com.yj2025.weixin.work.ConfigStorageAdpatder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WxErrorHandler implements InvocationHandler {

    private Object object;
    private ApplicationContext applicationContext;

    public WxErrorHandler(Object object, ApplicationContext applicationContext) {
        this.object = object;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            result = method.invoke(object, args);
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof WxErrorException) {
                captureWxErrorException((WxErrorException) ex.getTargetException());
            }
            throw ex;
        }
        return result;
    }

    public void captureWxErrorException(WxErrorException ex) {
        try {
            switch (ex.getError().getErrorCode()) {
                case 40084:
                    ConfigStorageAdpatder adpatder = getBean(ConfigStorageAdpatder.class);
                    adpatder.deleteTenantConfig();
                    break;
                case 40085:
                    getBean(WxCpTpConfigStorage.class).expireSuiteTicket();
                    break;
            }
            // TODO 处理微信特定异常码
        } catch (Exception e) {
            ;
        }
    }

    private <T> T getBean(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }
}
