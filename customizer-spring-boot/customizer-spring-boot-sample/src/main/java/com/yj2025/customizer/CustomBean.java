package com.yj2025.customizer;

public class CustomBean extends OriginalBean {
    @Override
    public void init() {
        this.setValue("override");
        System.out.println("value is override");
    }
}
