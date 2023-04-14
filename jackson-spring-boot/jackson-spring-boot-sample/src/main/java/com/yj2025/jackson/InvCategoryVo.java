package com.yj2025.jackson;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 货品分类树节点
 * Created by serv on 2017/5/20.
 */
@Data
public class InvCategoryVo {

    private String autoCode;

    private String code;

    private String parentCode;

    private String name;

    private List<String> codes = new ArrayList<>();

    private String inventoryTypeCode;

    private String inventoryTypeName;

    private String remark;

    private List<InvCategoryVo> children = new ArrayList<>();

}
