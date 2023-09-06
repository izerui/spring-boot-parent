package com.yj2025.sample;

import com.yj2025.tenant.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class DynamicDsService {

    @Autowired
    private ApplicationContext applicationContext;

    @Tenant("#{#entCode}")
    public void testDs(String entCode) {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        System.out.println("-");
    }
}
