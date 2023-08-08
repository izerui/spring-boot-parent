package com.yj2025.basic.web.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 自定义aop
 */
@Aspect
@Slf4j
@Component
public class YjLoadTimeImpl {

    //定义切点
    @Pointcut(value = "@annotation(com.yj2025.basic.web.annotation.YjLoadTime)")
    public void pointcut() {
    }

    /**
     * 执行切点之前
     */
    @Around("pointcut()")
    public Object doBefore(ProceedingJoinPoint joinPoint) {
        try {
            String uuid = UUID.randomUUID().toString();
            long start = System.currentTimeMillis();
            Object proceed = joinPoint.proceed();
            long loadTime = System.currentTimeMillis() - start;

            Class<?> clazz = joinPoint.getTarget().getClass();
            String targetName = clazz.getName();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getName();
            Method method = signature.getMethod();
            YjLoadTime annotation = method.getAnnotation(YjLoadTime.class);
            String operation = annotation.value();
            Object[] arguments = joinPoint.getArgs();

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            String entCode = getHeader(request, "entCode");
            String userCode = getHeader(request, "userCode");

            StringBuilder paramsBuf = new StringBuilder();
            for (Object arg : arguments) {
                paramsBuf.append(arg);
                paramsBuf.append("&");
            }
            Field field = proceed.getClass().getDeclaredField("unique");
            field.setAccessible(true);
            field.set(proceed, "【"+uuid+"】【企业："+entCode+"】【用户："+userCode+"】执行了【"+operation+"】,耗时 "+loadTime+" ms");
            log.info("【{}】【企业：{}】【用户：{}】执行了【{}】,类:{},方法名：{}", uuid, entCode, userCode, operation, targetName, methodName);
            log.info("【{}】【企业：{}】【用户：{}】执行了【{}】,参数:{}", uuid, entCode, userCode, operation, paramsBuf.toString());
            log.info("【{}】【企业：{}】【用户：{}】执行了【{}】,耗时 {} ms", uuid, entCode, userCode, operation, loadTime);
            return proceed;
        } catch (Throwable e) {
            log.info("around " + joinPoint + " with exception : " + e.getMessage());
        }
        return null;
    }

    private String getHeader(HttpServletRequest request, String header) {
        String value = request.getHeader(header);
        if (value != null) {
            value = URLDecoder.decode(value, StandardCharsets.UTF_8);
        }
        return value;
    }

}