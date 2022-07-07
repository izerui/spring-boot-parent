package com.yj2025.commons.vo;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
class PageSpringDataVo<T> {

    private int totalPages;

    private long totalElements;

    private int number;

    private int size;

    private List<T> content;

    private boolean hasNext;

    /**
     * 转换成PageVo 保持原对象不变
     *
     * @return
     */
    public static <T> PageVo<T> map(Object obj) {
        PageImpl page = (PageImpl) obj;
        PageVo<T> pageVo = new PageVo<T>();
        pageVo.setNumber(page.getNumber());
        pageVo.setSize(page.getSize());
        pageVo.setTotalElements(page.getTotalElements());
        pageVo.setTotalPages(page.getTotalPages());
        pageVo.setContent(page.getContent());
        pageVo.setHasNext(page.hasNext());
        return pageVo;
    }

    /**
     * 转换成PageVo 对象转换成T
     *
     * @return
     */
    public static <S, T> PageVo<T> map(Object obj, Function<S, T> mapper, Class<S> clazz) {
        Page<S> page = (Page<S>) obj;
        PageVo<T> pageVo = new PageVo<T>();
        pageVo.setNumber(page.getNumber());
        pageVo.setSize(page.getSize());
        pageVo.setTotalElements(page.getTotalElements());
        pageVo.setTotalPages(page.getTotalPages());
        pageVo.setContent(page.getContent().stream().map(mapper).collect(Collectors.toList()));
        pageVo.setHasNext(page.hasNext());
        return pageVo;
    }
}
