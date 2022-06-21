package com.yj2025.basic.controller.request;

import lombok.Data;

@Data
public abstract class BasicPageReq extends BasicReq{

    private PageReq page = new PageReq();
}
