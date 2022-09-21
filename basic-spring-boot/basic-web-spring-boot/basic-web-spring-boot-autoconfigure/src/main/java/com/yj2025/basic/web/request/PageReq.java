package com.yj2025.basic.web.request;


public class PageReq {

    /**
     * 页码
     */
    private Integer pageIndex = 0;

    /**
     * 每页条目数
     */
    private Integer pageSize = 20;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
