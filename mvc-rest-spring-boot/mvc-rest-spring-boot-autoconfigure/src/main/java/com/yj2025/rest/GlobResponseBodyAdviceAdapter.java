package com.yj2025.rest;

import com.ecworking.commons.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Base64Utils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by serv on 2017/2/17.
 */
@RestControllerAdvice
public class GlobResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object>, Constants {

    @Value("${spring.application.name:null}")
    private String applicationName;

    private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName()
            + ".ERROR";

    private Logger logger = LoggerFactory.getLogger(GlobResponseBodyAdviceAdapter.class);

    public GlobResponseBodyAdviceAdapter() {
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest httpRequest, ServerHttpResponse httpResponse) {

        HttpServletRequest request = ((ServletServerHttpRequest) httpRequest).getServletRequest();

        HttpServletResponse response = ((ServletServerHttpResponse) httpResponse).getServletResponse();

        if (response.getStatus() >= 400) {
            String requestApp = request.getHeader(CLIENT_NAME);
            if (StringUtils.isBlank(requestApp)) {
                requestApp = "web";
            }
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("success", false);
            resp.put("status", response.getStatus());
            resp.put("errCode", "exception");
            //转换异常
            Throwable throwable = getError(request);
            if (throwable == null) {
                if (body instanceof Map) {
                    resp.put("errMsg", ((Map) body).get("error"));
                    resp.put("data", ((Map) body).get("message"));
                    return resp;
                } else {
                    return body;
                }
            } else {
                logger.error("request:[" + getPath(request) + "]\t\trequestApp:[" + requestApp + "]\t\terror:[" + throwable.getMessage() + "]", throwable);
            }

            if (throwable.getClass().getName().equals("com.netflix.hystrix.exception.HystrixRuntimeException") && throwable.getCause() != null) {
                throwable = throwable.getCause();
            } else if (throwable.getClass().getName().equals("org.springframework.web.util.NestedServletException") && throwable.getCause() != null) {
                throwable = throwable.getCause();
            }

//            if (StringUtils.isNotEmpty(applicationName)
//                    && applicationName.equalsIgnoreCase("bboss-web")) {
//                ReflectionUtils.handleReflectionException((Exception) throwable);
//                return null;
//            }

            //自定义code异常
            if (throwable instanceof BusinessException) {
                //自定义异常status为200
                resp.put("status", 200);
                resp.put("errCode", ((BusinessException) throwable).getErrCode());
            } else if (throwable instanceof ExecutionException) {
                //自定义异常status为200
                resp.put("status", 200);
                resp.put("errCode", null);
            } else {
                resp.put("errCode", null);
            }

            String errMsg = throwable.getMessage();
            if (throwable instanceof HttpMessageNotWritableException) {
                resp.put("status", 200);
                errMsg = ((HttpMessageNotWritableException) throwable).getRootCause().getMessage();
            } else if (throwable instanceof RuntimeException || throwable instanceof ExecutionException) {
                resp.put("status", 200);
                String message = throwable.getMessage();
                if (StringUtils.isNotBlank(errMsg) && message.lastIndexOf("Exception:") > -1) {
                    errMsg = StringUtils.trim(message.substring(message.lastIndexOf("Exception:") + 10));
                }
            }
            if (throwable instanceof HttpMessageNotReadableException) {
                resp.put("status", 200);
                errMsg = "请求中包含错误格式的数据,请检查";
            }
            if (throwable instanceof NullPointerException) {
                resp.put("status", 200);
                errMsg = "系统发生了未知的错误";
            }
            if (throwable.getClass().getName().equals("com.netflix.zuul.exception.ZuulException")) {
                resp.put("status", 200);
                errMsg = "请求服务暂时不可用,请稍后重试";
            }

            resp.put("errMsg", errMsg);
            resp.put("data", body);
            resp.put("exceptionType", throwable.getClass().getName());


            String clientType = request.getHeader(CLIENT_TYPE);
            if (clientType != null && FEIGN_REQUEST_TYPE.equals(clientType)) {
                //feign 请求返回异常堆栈信息
                resp.put(EXCEPTION_SERIALIZABLE, Base64Utils.encodeToString(SerializationUtils.serialize(throwable)));
            } else {
                // 浏览器请求统一返回200状态码
                response.setStatus(HttpStatus.OK.value());
            }

            return resp;
        }
        return body;
    }


    private String getPath(HttpServletRequest request) {
        return (String) request.getAttribute("javax.servlet.error.request_uri");
    }

    public Throwable getError(HttpServletRequest request) {
        Throwable exception = (Throwable) request.getAttribute(ERROR_ATTRIBUTE);
        if (exception == null) {
            exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
        }
        return exception;
    }


}
