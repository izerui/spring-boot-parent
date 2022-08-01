package com.yj2025.basic.web.request;


public abstract class BasicPageReq extends BasicReq {

    private PageReq page = new PageReq();

    public PageReq getPage() {
        return page;
    }

    public void setPage(PageReq page) {
        this.page = page;
    }
}
