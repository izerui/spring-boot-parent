package com.yj2025.command;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class Context implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Context.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return Context.applicationContext.getBean(beanClass);
    }

    public static void dispatchEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }


    public static class WebContext {

        private static HttpServletRequest getRequest() {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                return ((ServletRequestAttributes) requestAttributes).getRequest();
            }
            throw new RuntimeException("非web请求，无法获取request对象");
        }

        public static String getRequestHeader(String header) {
            return getRequest().getHeader(header);
        }

        public static String getEntCode(){
            return getRequest().getHeader(URLDecoder.decode("entCode", StandardCharsets.UTF_8));
        }

        public static String getEntName(){
            return getRequest().getHeader(URLDecoder.decode("entName", StandardCharsets.UTF_8));
        }

        public static String getUserCode(){
            return getRequest().getHeader(URLDecoder.decode("userCode", StandardCharsets.UTF_8));
        }

        public static String getUserName(){
            return getRequest().getHeader(URLDecoder.decode("userName", StandardCharsets.UTF_8));
        }

        public static String getAccountCode(){
            return getRequest().getHeader(URLDecoder.decode("accountCode", StandardCharsets.UTF_8));
        }

        public static String getAccountName(){
            return getRequest().getHeader(URLDecoder.decode("accountName", StandardCharsets.UTF_8));
        }
    }
}
