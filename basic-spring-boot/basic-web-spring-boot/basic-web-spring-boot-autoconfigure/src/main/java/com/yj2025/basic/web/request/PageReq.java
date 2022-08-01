package com.yj2025.basic.web.request;

import lombok.Data;

@Data
public class PageReq {

    /**
     * 页码
     */
    protected Integer pageIndex = 0;

    /**
     * 每页条目数
     */
    protected Integer pageSize = 20;
}
