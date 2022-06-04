package com.yj2025.weixin.work22.ureport;

import com.bstek.ureport.console.UReportServlet;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

@ImportResource("classpath:ureport-console-context.xml")
@Configuration
public class UreportConfiguration {

    @Bean
    public ServletRegistrationBean ureportServlet() {
        return new ServletRegistrationBean(new UReportServlet(), "/ureport/*");
    }

}
