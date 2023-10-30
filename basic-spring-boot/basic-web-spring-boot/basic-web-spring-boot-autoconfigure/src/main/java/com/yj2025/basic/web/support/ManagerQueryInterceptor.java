package com.yj2025.basic.web.support;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Properties;
import java.util.function.Supplier;

/**
 * mysql拦截器:
 * 当以维护人员登录系统(动态验证码、超级密码)进行操作时,拦截相关insert、update、delete操作，并抛出权限异常。
 * 使用方式:
 * 修改jdbc连接串,添加属性`queryInterceptors`并指定拦截器类路径`com.yj2025.basic.web.support.ManagerQueryInterceptor`,多个以逗号分隔.
 * 示例: spring.datasource.url=jdbc:mysql://10.96.157.80:3306/development?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false&useSSL=false&queryInterceptors=com.yj2025.basic.web.support.ManagerQueryInterceptor
 */
public class ManagerQueryInterceptor implements QueryInterceptor {

    private Log log;

    @Override
    public QueryInterceptor init(MysqlConnection conn, Properties props, Log log) {
        this.log = log;
        return this;
    }

    @Override
    public <T extends Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
        String sqlText = sql.get();
        sqlText = sqlText.toLowerCase();
        log.logInfo(sqlText);
        if (sqlText.startsWith("update")
                || sqlText.startsWith("insert") || sqlText.startsWith("delete")) {
            if (isShadow()) {
                throw new PermissionDeniedDataAccessException("没有权限操作! 请注意你的行为!!!", null);
            }
        }
        return null;
    }

    private boolean isShadow() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return Boolean.valueOf(request.getHeader("shadow"));
        }
        return false;
    }

    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    @Override
    public boolean executeTopLevelOnly() {
        return false;
    }

    @Override
    public void destroy() {

    }

    @Override
    public <T extends Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
        return null;
    }
}
