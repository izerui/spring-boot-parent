package com.yj2025.basic.web.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yj2025.basic.web.support.AuthAware;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import io.vavr.control.Option;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * @author liuyuhua
 */
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
    private String sortDirection;

    /**
     * 默认的排序方式
     *
     * @return
     */
    protected abstract Sort withDefaultSort();


    @ApiParam(hidden = true)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public final PageRequest getPageRequest() {
        Sort sort = withDefaultSort();
        if (StringUtils.isNotBlank(sortField)) {
            if (StringUtils.isNotBlank(sortDirection)) {
                sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
            } else {
                sort = Sort.by(sortField);
            }
        }
        return PageRequest.of(this.pageIndex, this.pageSize, sort);
    }

    @Data
    public static class OrderRequest {
        @Nullable
        String property;
        @Nullable
        Sort.Direction direction;

        @JsonIgnore
        public Sort.Order toOrder() {
            return Option.when(property != null, () -> Option
                    .when(direction == null, Sort.Order.by(property))
                    .getOrElse(
                            Option.when(direction.isAscending(), Sort.Order.asc(property))
                                    .getOrElse(Sort.Order.desc(property))
                    )).getOrNull();
        }
    }
}
