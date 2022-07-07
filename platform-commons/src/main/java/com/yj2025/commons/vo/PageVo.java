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
public class PageVo<T> {

    private int totalPages;

    private long totalElements;

    private int number;

    private int size;

    private List<T> content;

    private boolean hasNext;

    /**
     * 转换成PageVo 保持原对象不变
     *
     * @param page
     * @param <S>
     * @return
     */
    public static <S> PageVo<S> map(Object page) {
        try {
            Class pageImplClass = Class.forName("org.springframework.data.domain.PageImpl");
            Class pageClass = Class.forName("com.baomidou.mybatisplus.extension.plugins.pagination.Page");
            if (pageImplClass.isAssignableFrom(page.getClass())) {
                return PageSpringDataVo.map(page);
            } else if (pageClass.isAssignableFrom(page.getClass())) {
                return PageBaomidouVo.map(page);
            }
            throw new RuntimeException("不支持");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }

    }

    /**
     * 转换成PageVo 对象转换成T
     *
     * @param page
     * @param mapper
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> PageVo<T> map(Object page, Function<S, T> mapper, Class<S> clazz) {
        try {
            Class pageImplClass = Class.forName("org.springframework.data.domain.PageImpl");
            Class pageClass = Class.forName("com.baomidou.mybatisplus.extension.plugins.pagination.Page");
            if (pageImplClass.isAssignableFrom(page.getClass())) {
                return PageSpringDataVo.map(page, mapper, clazz);
            } else if (pageClass.isAssignableFrom(page.getClass())) {
                return PageBaomidouVo.map(page, mapper, clazz);
            }
            throw new RuntimeException("不支持");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public static <S, T> PageVo<T> map(Page<S> page, Function<S, T> mapper) {
        PageVo<T> pageVo = new PageVo<T>();
        pageVo.setNumber(page.getNumber());
        pageVo.setSize(page.getSize());
        pageVo.setTotalElements(page.getTotalElements());
        pageVo.setTotalPages(page.getTotalPages());
        pageVo.setContent(page.getContent().stream().map(mapper).collect(Collectors.toList()));
        pageVo.setHasNext(page.hasNext());
        return pageVo;
    }

    public static <S, T> PageVo<T> map(Page<S> page, List<T> list) {
        PageVo<T> pageVo = new PageVo<T>();
        pageVo.setNumber(page.getNumber());
        pageVo.setSize(page.getSize());
        pageVo.setTotalElements(page.getTotalElements());
        pageVo.setTotalPages(page.getTotalPages());
        pageVo.setContent(list);
        pageVo.setHasNext(page.hasNext());
        return pageVo;
    }
}
