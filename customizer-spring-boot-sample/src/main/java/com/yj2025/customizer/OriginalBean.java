package com.yj2025.customizer;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OriginalBean {

    @PostConstruct
    public void init() {
        System.out.println("我是原始的,但是不会执行，因为我已经被别人覆盖了，人家不用我了");
    }

    private String value = "default";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
