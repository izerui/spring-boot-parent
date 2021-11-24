package com.yj2025.override;

public class OverrideBean extends OriginalBean {
    public void init() {
        this.setValue("override");
        System.out.println("value is override");
    }
}
