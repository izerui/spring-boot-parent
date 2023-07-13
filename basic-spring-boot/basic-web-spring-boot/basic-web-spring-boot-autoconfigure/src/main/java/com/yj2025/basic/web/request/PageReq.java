package com.yj2025.basic.web.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yj2025.basic.web.support.AuthAware;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vavr.control.Option;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

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


    @Parameter(hidden = true)
    @JsonIgnore
    @Schema(hidden = true)
    public final PageRequest getPageRequest() {
        Sort sort = withDefaultSort();
        if (StringUtils.isNotBlank(orderBy)) {
            if (StringUtils.isNotBlank(orderByDirection)) {
                sort = Sort.by(Sort.Direction.fromString(orderByDirection), orderBy, "id");
            } else {
                sort = Sort.by(orderBy, "id");
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
