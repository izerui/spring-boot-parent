package com.yj2025.basic.web.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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

    @JsonIgnore
    public Sort getSort() {
        if (StringUtils.isNotBlank(sortField)) {
            return Sort.by(sortDirection, sortField);
        }
        return getDefaultSort();
    }

    @JsonIgnore
    public Pageable getPageable() {
        return PageRequest.of(this.pageIndex, this.pageSize, getSort());
    }
}
