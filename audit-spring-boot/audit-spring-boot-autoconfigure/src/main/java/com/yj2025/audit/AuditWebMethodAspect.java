package com.yj2025.audit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by serv on 2016/12/8.
 */
@Aspect
@Slf4j
public class AuditWebMethodAspect implements DisposableBean {

    private ObjectProvider<AuditContext> auditContextProvider;

    private String application;

    private final static ObjectMapper OBJECT_MAPPER;
    private final static ThreadPoolExecutor POOL_EXECUTOR;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // audit 操作日志消息处理线程池
        POOL_EXECUTOR = new ThreadPoolExecutor(
                // 核心线程数
                8,
                // 最大线程数
                30,
                // 大于核心线程数量的空闲线程于30毫秒后销毁
                0,
                // 空闲超时的单位毫秒
                TimeUnit.MILLISECONDS,
                // 有界的任务队列缓冲区 大小为1000个等待任务 (当核心线程空闲后，放入核心线程，缓冲区满了后，新任务直接以最大线程限制内去执行。如果最大线程也满了，缓冲区也满了，则执行相应的拒绝策略)
                new ArrayBlockingQueue<Runnable>(1000),
                // 线程工厂
                Executors.defaultThreadFactory(),
                // 都满了的情况下打印警告，并直接丢弃
                (r, executor) -> {
                    log.warn("audit 操作审计被丢弃,线程池处理不过来! Task " + r.toString() +
                            " rejected from " +
                            executor.toString());
                });
    }

    public AuditWebMethodAspect(ObjectProvider<AuditContext> auditContextProvider, String application) {
        this.auditContextProvider = auditContextProvider;
        this.application = application;
    }


    @SuppressWarnings("unchecked")
    @Around("@annotation(io.swagger.annotations.ApiOperation)")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return point.proceed();
        }

        if (auditContextProvider.stream().count() == 0L) {
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
            record.setGroupType("WEB请求审计日志");
            if (api != null) {
                if (api.description() != null && !"".equals(api.description())) {
                    record.setGroupType(api.description());
                } else if (api.value() != null && !"".equals(api.value())) {
                    record.setGroupType(api.value());
                } else if (api.tags() != null && api.tags().length > 0) {
                    record.setGroupType(String.join(",", api.tags()));
                }
            }
            record.setApplication(application);
            record.setBegin(new Date());
            record.setSignature(point.getSignature().toString());

            record.setIp(getIpAddress(request));
            record.setUrl(request.getRequestURL().toString());

            Map<String, Object> map = new HashMap();
            List<String> params = null;
            if (point.getArgs() != null && point.getArgs().length > 0) {
                params = Arrays.stream(point.getArgs()).map(o -> {
                    try {
                        if (o == null) {
                            return "null";
                        }
                        if (o instanceof MultipartFile
                                || o instanceof HttpServletRequest
                                || o instanceof HttpServletResponse) {
                            return o.toString();
                        }
                        return OBJECT_MAPPER.writeValueAsString(o);
                    } catch (Exception e) {
                        log.warn(o.getClass() + " to json error!");
                    }
                    return o.getClass() + " to json error! value is:" + o.toString();
                }).collect(Collectors.toList());
            }
            map.put("params", params);
            Map<String, String> headerMap = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headerMap.put(headerName, headerValue);
            }
            map.put("heads", headerMap);
            map.put("query", request.getQueryString());
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
                        record.setExceptionClassType(e.getClass().getName());
                    }
                } catch (Exception ex) {
                    ;
                }
                throw e;
            } finally {
                //操作结束时间
                record.setEnd(new Date());
                try {
                    auditContextProvider.forEach(auditContext -> {
//                        auditContext.record(record);
                        POOL_EXECUTOR.execute(() -> auditContext.record(record));
                    });
                } catch (Exception ex) {
                    log.warn(ex.getMessage());
                }
            }
            return reaultValue;

        } catch (Exception e) {
//            if (e != null) {
//                log.error("audit-error:" + e.getMessage(), e);
//            }
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

    @Override
    public void destroy() throws Exception {
        POOL_EXECUTOR.shutdown();
    }
}
