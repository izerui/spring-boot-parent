package com.yj2025.basic.service.firewall;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationContext;

@Aspect
public class ManagerFirewallAspect {

    private final ApplicationContext applicationContext;

    public ManagerFirewallAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Before("@annotation(org.springframework.stereotype.Service)")
    public void before(JoinPoint joinPoint) throws Throwable {
        System.out.println("dddddddddd");

    }


}
