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

    @JsonIgnore
    protected abstract Sort getDefaultSort();

    /**
     * 可覆盖更改排序字段名
     *
     * @return
     */
    protected String[] getSortFields() {
        return new String[]{sortField};
    }


    /**
     * 可覆盖更改排序方向字段名
     * @return
     */
    protected Sort.Direction getSortDirection() {
        return sortDirection;
    }

    @JsonIgnore
    public Sort getSort() {
        String[] sortFields = getSortFields();
        if (sortFields != null && ArrayUtils.isNotEmpty(sortFields)) {
            return Sort.by(getSortDirection(), sortFields);
        }
        return getDefaultSort();
    }

    @JsonIgnore
    public Pageable getPageable() {
        return PageRequest.of(this.pageIndex, this.pageSize, getSort());
    }
}
