package com.yj2025.sample;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.yj2025.dynamic.TenantDynamicDataSource;
import com.yj2025.tenant.Tenant;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class DynamicDsService {

    @Autowired
    private ApplicationContext applicationContext;

    @Tenant("#{#entCode}")
    @DS("master")
    public void testMaster(String entCode) {
        TenantDynamicDataSource dataSource = (TenantDynamicDataSource) applicationContext.getBean(DataSource.class);
        ItemDataSource ds = (ItemDataSource) dataSource.determineDataSource();
        HikariDataSource hkds = (HikariDataSource) ds.getDataSource();
        System.out.println("master->  entCode: "+entCode + "  ds: " + ds.getName() + "  url: " + hkds.getJdbcUrl() );
    }

    @Tenant("#{#entCode}")
    @DS("read")
    public void testRead(String entCode) {
        TenantDynamicDataSource dataSource = (TenantDynamicDataSource) applicationContext.getBean(DataSource.class);
        ItemDataSource ds = (ItemDataSource) dataSource.determineDataSource();
        HikariDataSource hkds = (HikariDataSource) ds.getDataSource();
        System.out.println("read->  entCode: "+entCode + "  ds: " + ds.getName() + "  url: " + hkds.getJdbcUrl() );
    }
}
