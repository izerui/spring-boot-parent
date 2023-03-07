package com.yj2025.commons.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class PageBaomidouVO<T> {

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
    public static <T> PageVO<T> map(Object obj) {
        Page page = (Page) obj;
        PageVO<T> pageVo = new PageVO<T>();
        pageVo.setNumber((int) page.getCurrent());
        pageVo.setSize((int) page.getSize());
        pageVo.setTotalElements(page.getTotal());
        pageVo.setTotalPages((int) page.getPages());
        pageVo.setContent(page.getRecords());
        pageVo.setHasNext(page.hasNext());
        return pageVo;
    }

    /**
     * 转换成PageVo 对象转换成T
     *
     * @return
     */
    public static <S, T> PageVO<T> map(Object obj, Function<S, T> mapper, Class<S> clazz) {
        Page<S> page = (Page<S>) obj;
        PageVO<T> pageVo = new PageVO<T>();
        pageVo.setNumber((int) page.getCurrent());
        pageVo.setSize((int) page.getSize());
        pageVo.setTotalElements(page.getTotal());
        pageVo.setTotalPages((int) page.getPages());
        pageVo.setContent(page.getRecords().stream().map(mapper).collect(Collectors.toList()));
        pageVo.setHasNext(page.hasNext());
        return pageVo;
    }
}
