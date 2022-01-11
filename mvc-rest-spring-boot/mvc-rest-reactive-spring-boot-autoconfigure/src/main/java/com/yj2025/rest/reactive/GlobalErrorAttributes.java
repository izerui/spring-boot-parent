package com.yj2025.rest.reactive;


import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Base64Utils;
import org.springframework.util.SerializationUtils;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GlobalErrorAttributes extends DefaultErrorAttributes implements Constants {


    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
//        原生的错误信息
//        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        Throwable throwable = getError(request);
        if (throwable instanceof HystrixRuntimeException && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }

        log.error(throwable.getMessage(), throwable);
        Map<String,Object> errorResponseMap = new HashMap<>();
        errorResponseMap.put("success", false);
        errorResponseMap.put("errCode", "exception");
        String errMsg = throwable.getMessage();
        if (throwable instanceof HttpMessageNotWritableException) {
            errMsg = ((HttpMessageNotWritableException) throwable).getRootCause().getMessage();
        }
        if (throwable instanceof HttpMessageNotReadableException) {
            errMsg = "请求中包含错误格式的数据,请检查";
        }
        if (throwable instanceof NullPointerException) {
            errMsg = "系统发生了未知的错误";
        }
        if (throwable.getClass().getName().equals("com.netflix.zuul.exception.ZuulException")) {
            errMsg = "请求服务暂时不可用,请稍后重试";
        }
        errorResponseMap.put("errMsg", errMsg);
        errorResponseMap.put("exceptionType", throwable.getClass().getName());

        //feign 请求
        String clientType = request.headers().firstHeader(CLIENT_TYPE);
        if (clientType != null && FEIGN_REQUEST_TYPE.equals(clientType)) {
            errorResponseMap.put(EXCEPTION_SERIALIZABLE, Base64Utils.encodeToString(SerializationUtils.serialize(throwable)));
        }

        return errorResponseMap;
    }
}
