package com.yj2025.rest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
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
import org.springframework.util.SerializationUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by serv on 2017/2/17.
 */
@RestControllerAdvice
public class GlobResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object>, Constants {

    @Value("${spring.application.name:null}")
    private String applicationName;

    private ErrorAttributes errorAttributes;

    private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName()
            + ".ERROR";

    private Logger logger = LoggerFactory.getLogger(GlobResponseBodyAdviceAdapter.class);

    public GlobResponseBodyAdviceAdapter(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest httpRequest, ServerHttpResponse httpResponse) {
        HttpServletRequest request = ((ServletServerHttpRequest) httpRequest).getServletRequest();
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);

        HttpServletResponse response = ((ServletServerHttpResponse) httpResponse).getServletResponse();

        if (response.getStatus() >= 400) {
            String requestApp = request.getHeader(CLIENT_NAME);
            if (StringUtils.isBlank(requestApp)) {
                requestApp = "web";
            }
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("success", false);
            resp.put("status", response.getStatus());
            resp.put("errCode", String.valueOf(response.getStatus()));
            //转换异常
            Throwable throwable = errorAttributes.getError(servletWebRequest);
            if (throwable == null) {
                response.setStatus(HttpStatus.OK.value());
                Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(servletWebRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.values()));
                logger.error(errorAttributes.toString());
                if (body instanceof Map) {
                    resp.put("errMsg", ((Map) body).get("error"));
                    resp.put("data", ((Map) body).get("message"));
                    resp.putAll(errorAttributes);
                    return resp;
                } else {
                    return body;
                }
            } else {
                // 不再输出异常堆栈信息，统一参考 dispatcherServlet 中的异常打印
                logger.error("客户端: {} 请求: {} 出错: {}", requestApp, getPath(request), throwable.getMessage());
            }

            if (throwable.getClass().getName().equals("com.netflix.hystrix.exception.HystrixRuntimeException") && throwable.getCause() != null) {
                throwable = throwable.getCause();
            } else if (throwable.getClass().getName().equals("org.springframework.web.util.NestedServletException") && throwable.getCause() != null) {
                throwable = throwable.getCause();
            }
            Throwable rootCause = ExceptionUtils.getRootCause(throwable);
            if (rootCause != null) {
                throwable = rootCause;
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
            if (throwable instanceof MethodArgumentNotValidException) {
                resp.put("status", 200);
                List<ObjectError> allErrors = ((MethodArgumentNotValidException) throwable).getBindingResult().getAllErrors();
                errMsg = allErrors.stream().map(objectError -> objectError.getDefaultMessage()).collect(Collectors.joining(";"));
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
                try {
                    //feign 请求返回异常堆栈信息 ，并且将发生异常的所属应用名一并返回
                    resp.put(EXCEPTION_APPLICATION_NAME, applicationName);
                    resp.put(EXCEPTION_SERIALIZABLE, Base64Utils.encodeToString(SerializationUtils.serialize(throwable)));
                }catch (Exception e) {
                    ;
                }
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

}
