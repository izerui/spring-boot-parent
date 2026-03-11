package com.yj2025.jdbc.dialect.flag;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueryFlag {
    private String tablePrefix;
    private boolean isComment;
    private String value;

    public String getTablePrefix() {
        if (tablePrefix == null) {
            return "";
        }
        return tablePrefix;
    }
}
