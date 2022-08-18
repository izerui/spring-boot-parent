package com.yj2025.basic.web.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public abstract class PageReq {

    /**
     * 页码
     */
    private Integer pageIndex = 0;

    /**
     * 每页条目数
     */
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
