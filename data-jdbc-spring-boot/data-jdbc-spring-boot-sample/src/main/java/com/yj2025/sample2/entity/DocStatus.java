package com.yj2025.sample2.entity;

import lombok.Getter;

@Getter
public enum DocStatus {
    DRAFT("草稿"),
    AUDITING("审核中");

    private final String description;

    DocStatus(String description) {
        this.description = description;
    }
}
