package com.yj2025.oauth2.server.advice;

import com.yj2025.oauth2.security.support.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.yj2025.oauth2.security.support.RespVo.error;
import static com.yj2025.oauth2.security.support.RespVo.success;

@Slf4j
@RestControllerAdvice
public class GlobalResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object> {

    private ErrorAttributes errorAttributes;
    private List<String> ignoreWrapPathMatchers = new ArrayList<>();
    private PathMatcher pathMatcher = new AntPathMatcher();

    public GlobalResponseBodyAdviceAdapter(ErrorAttributes errorAttributes, String... ignoreWrapPathMatchers) {
        this.errorAttributes = errorAttributes;
        if (ignoreWrapPathMatchers != null) {
            for (String ignoreWrapPathMatcher : ignoreWrapPathMatchers) {
                this.ignoreWrapPathMatchers.add(ignoreWrapPathMatcher);
            }
        }
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 判断当前path路径是否需要使用RespVo包装
     *
     * @param path
     * @return
     */
    private boolean isNeedWrap(String path) {
        for (String ignoreWrapPathMatcher : ignoreWrapPathMatchers) {
            if (pathMatcher.match(ignoreWrapPathMatcher, path)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest httpRequest, ServerHttpResponse httpResponse) {
        HttpServletRequest request = ((ServletServerHttpRequest) httpRequest).getServletRequest();
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);
        HttpServletResponse response = ((ServletServerHttpResponse) httpResponse).getServletResponse();
        if (response.getStatus() <= 200) {
            if (body instanceof RespVo) {
                return body;
            } else if (isNeedWrap(URI.create(request.getRequestURL().toString()).getPath())) {
                return success(body);
            }
        } else if (response.getStatus() >= 400) {
            response.setStatus(HttpStatus.OK.value());
            Throwable throwable = errorAttributes.getError(servletWebRequest);
            if (throwable == null) {
                Map<String, Object> map = errorAttributes.getErrorAttributes(servletWebRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.values()));
                return error(String.valueOf(map.get("error")), String.valueOf(map.get("message")));
            }
            String errCode = String.valueOf(response.getStatus());
            if (throwable instanceof OAuth2Exception) {
                errCode = ((OAuth2Exception) throwable).getOAuth2ErrorCode();
            }
            return error(errCode, throwable.getMessage());
        }
        return body;
    }


}
