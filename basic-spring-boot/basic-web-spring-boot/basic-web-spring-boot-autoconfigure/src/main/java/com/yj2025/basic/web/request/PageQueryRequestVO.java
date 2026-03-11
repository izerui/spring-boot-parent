package com.yj2025.basic.web.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


/**
 * @author liuyuhua
 */
@Data
public abstract class PageQueryRequestVO extends BaseQueryRequestVO {

    @Schema(description = "页码不能为空,起始页从0开始", name = "pageIndex")
    private Integer pageIndex = 0;

    @Schema(description = "每页条目数不能为空 每页条目数最大不能超过2000条", name = "pageSize")
    private Integer pageSize = 20;

    @Schema(description = "排序字段", name = "orderBy")
    private String orderBy;

    @Schema(description = "排序方向，ASC 升序，DESC 降序", name = "orderByDirection")
    private String orderByDirection;

    @Schema(description = "排序使用时，是否包含id字段", name = "withId")
    private boolean withId = true;

    /**
     * 默认的排序方式
     *
     * @return
     */
    protected Sort withDefaultSort() {
        return Sort.unsorted();
    }

    @Parameter(hidden = true)
    @JsonIgnore
    @Schema(hidden = true)
    public final PageRequest getPageRequest() {
        return PageRequest.of(this.pageIndex, this.pageSize, getSort());
    }

    @Parameter(hidden = true)
    @JsonIgnore
    @Schema(hidden = true)
    public final Sort getSort() {
        Sort sort = withDefaultSort();
        if (StringUtils.isNotBlank(orderBy)) {
            if (StringUtils.isNotBlank(orderByDirection)) {
                sort = Sort.by(Sort.Direction.fromString(orderByDirection), orderBy);
            } else {
                sort = Sort.by(orderBy);
            }
        }
        return wrapSort(sort);
    }

    protected Sort wrapSort(Sort sort) {
        return this.isWithId() ? sort.and(Sort.by("id")) : sort;
    }

    /**
     * 填充排序信息
     *
     * @param orderBy
     * @param orderByDirection
     */
    public final void fillSortInfo(String orderBy, String orderByDirection) {
        this.orderBy = orderBy;
        this.orderByDirection = orderByDirection;
    }

    /**
     * 填充排序信息
     *
     * @param orderBy
     * @param orderByDirection
     */
    public final void fillSortInfo(String orderBy, String orderByDirection, boolean withId) {
        this.orderBy = orderBy;
        this.orderByDirection = orderByDirection;
        this.withId = withId;
    }
}
