package com.yj2025.basic.web.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yj2025.basic.web.support.AuthAware;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public abstract class PageReq implements AuthAware {

    /**
     * 页码
     */
    @NotNull(message = "页码不能为空,起始页从0开始")
    private Integer pageIndex = 0;

    /**
     * 每页条目数
     */
    @NotNull(message = "每页条目数不能为空")
    @Max(value = 2000, message = "每页条目数最大不能超过2000条")
    private Integer pageSize = 20;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方向
     */
    private Sort.Direction sortDirection = Sort.Direction.DESC;

    /**
     * 默认的排序方式
     * @return
     */
    protected abstract Sort withDefaultSort();


    /**
     * 可覆盖更改排序字段名
     *
     * @return
     */
    protected String[] withSortFields() {
        return new String[]{sortField};
    }


    /**
     * 可覆盖更改排序方向字段名
     *
     * @return
     */
    protected Sort.Direction withSortDirection() {
        return sortDirection;
    }

    @JsonIgnore
    public final Sort getSort() {
        String[] sortFields = withSortFields();
        if (sortFields != null && ArrayUtils.isNotEmpty(sortFields)) {
            return Sort.by(withSortDirection(), sortFields);
        }
        return withDefaultSort();
    }

    @JsonIgnore
    public final Pageable getPageable() {
        return PageRequest.of(this.pageIndex, this.pageSize, getSort());
    }
}
