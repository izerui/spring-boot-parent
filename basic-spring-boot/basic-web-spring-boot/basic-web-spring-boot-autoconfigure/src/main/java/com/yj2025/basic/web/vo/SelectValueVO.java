package com.yj2025.basic.web.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SelectValueVO implements Serializable {

    /**
     * 值
     */
    private String value;

    /**
     * 显示名称
     */
    private String label;

    private Long count;

    private List<SelectValueVO> children = new ArrayList();

    public SelectValueVO(String value, String label, List<SelectValueVO> children) {
        this.value = value;
        this.label = label;
        this.children = children;
    }

    public SelectValueVO(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
