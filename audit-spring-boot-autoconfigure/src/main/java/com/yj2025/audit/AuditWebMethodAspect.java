package com.yj2025.audit;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by serv on 2016/12/8.
 */
@Aspect
@Slf4j
public class AuditWebMethodAspect {

    private AuditContext auditContext;

    private String application;

    public AuditWebMethodAspect(AuditContext auditContext, String application) {
        this.auditContext = auditContext;
        this.application = application;
    }


    @SuppressWarnings("unchecked")
    @Around("@annotation(io.swagger.annotations.ApiOperation)")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return point.proceed();
        }

        try {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            ApiOperation recordAudit = method.getAnnotation(ApiOperation.class);
            if (recordAudit == null || method.getAnnotation(IgnoreAudited.class) != null) {
                return point.proceed();
            }
            Api api = point.getTarget().getClass().getDeclaredAnnotation(Api.class);

            Record record = new Record();
            record.setType("WEB请求审计日志");
            if (api != null) {
                if (api.description() != null && !"".equals(api.description())) {
                    record.setType(api.description());
                } else if (api.value() != null && !"".equals(api.value())) {
                    record.setType(api.value());
                } else if (api.tags() != null && api.tags().length > 0) {
                    record.setType(api.tags().toString());
                }
            }
            record.setApplication(application);
            record.setBegin(new Date());
            record.setSignature(point.getSignature().toString());

            record.setIp(getIpAddress(request));
            record.setUrl(request.getRequestURL().toString());

            Map<String, Object> map = new HashMap();
            List<Object> params = new ArrayList<>();
            if (point.getArgs() != null && point.getArgs().length > 0) {
                for (Object arg : point.getArgs()) {
                    if (!(arg instanceof MultipartFile)) {
                        params.add(arg);
                    }
                }
            }
            map.put("params", params.toArray());
            record.setInfo(map);

            record.setAccountCode(request.getHeader("accountCode"));
            if (request.getHeader("accountName") != null) {
                record.setAccountName(URLDecoder.decode(request.getHeader("accountName"), "UTF-8"));
            }
            record.setEntCode(request.getHeader("entCode"));
            if (request.getHeader("entName") != null) {
                record.setEntName(URLDecoder.decode(request.getHeader("entName"), "UTF-8"));
            }
            record.setUserCode(request.getHeader("userCode"));
            if (request.getHeader("userName") != null) {
                record.setUserName(URLDecoder.decode(request.getHeader("userName"), "UTF-8"));
            }

            record.setName(recordAudit.value());

            Object reaultValue = null;

            try {
                reaultValue = point.proceed();
                //执行成功状态
                record.setSuccess(true);
            } catch (Throwable e) {
                //执行成功状态
                record.setSuccess(false);
                //错误信息
                try {
                    if (e != null) {
                        record.setException(e.getMessage());
                    }
                } catch (Exception ex) {
                    ;
                }
                throw e;
            } finally {
                //操作结束时间
                record.setEnd(new Date());
                auditContext.record(record);
            }
            return reaultValue;

        } catch (Exception e) {
            log.error("audit-error:" + e.getMessage(), e);
            throw e;
        }

    }


    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }


    /**
     * 通过Request 获取ip
     *
     * @return String
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.trim();
    }


}
