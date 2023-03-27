package com.yj2025.commons.vo;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by serv on 2017/6/27.
 */
@Data
public class PageVO<T> {

    private int totalPages;

    private long totalElements;

    private int number;

    private int size;

    private List<T> content;

    private boolean hasNext;

    public PageVO(List<T> content, long totalElements, int totalPages, int number, int size) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.number = number;
        this.size = size;
        this.hasNext = this.number + 1 < this.getTotalPages();
    }

    public PageVO() {
        this.content = List.of();
        this.totalElements = 0;
        this.totalPages = 0;
        this.number = 0;
        this.size = 0;
        this.hasNext = false;
    }

    public static <S, T> PageVO<T> map(Page<S> page, Function<S, T> mapper) {
        PageVO<T> pageVo = new PageVO<T>();
        pageVo.setNumber(page.getNumber());
        pageVo.setSize(page.getSize());
        pageVo.setTotalElements(page.getTotalElements());
        pageVo.setTotalPages(page.getTotalPages());
        pageVo.setContent(page.getContent().stream().map(mapper).collect(Collectors.toList()));
        pageVo.setHasNext(page.hasNext());
        return pageVo;
    }

    public static <S, T> PageVO<T> map(Page<S> page, List<T> list) {
        PageVO<T> pageVo = new PageVO<T>();
        pageVo.setNumber(page.getNumber());
        pageVo.setSize(page.getSize());
        pageVo.setTotalElements(page.getTotalElements());
        pageVo.setTotalPages(page.getTotalPages());
        pageVo.setContent(list);
        pageVo.setHasNext(page.hasNext());
        return pageVo;
    }
}
