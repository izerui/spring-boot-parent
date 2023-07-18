package com.yj2025.basic.web.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @author liuyuhua
 */
@Data
public abstract class PageQueryRequestVO extends BaseQueryRequestVO {

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
    private String orderBy;

    /**
     * 排序方向
     */
    private String orderByDirection;

    /**
     * 默认的排序方式
     *
     * @return
     */
    protected abstract Sort withDefaultSort();

    /**
     * 所有排序后面跟随的固定排序, 默认为空
     *
     * @return
     */
    protected Sort withFixedSort() {
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
                sort = Sort.by(Sort.Direction.fromString(orderByDirection), orderBy, "id");
            } else {
                sort = Sort.by(orderBy, "id");
            }
        }
        return sort;
    }
}
