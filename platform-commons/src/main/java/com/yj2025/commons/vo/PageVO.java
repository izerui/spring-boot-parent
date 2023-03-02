package com.yj2025.commons.vo;

import io.vavr.control.Try;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    public PageVO(List<T> content, Pageable pageable, long total) {
        this.content = content;
        this.size = pageable.getPageSize();
        this.totalElements = total;
        this.number = pageable.getPageNumber();
        this.totalPages = getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());;
        this.hasNext = this.number + 1 < this.getTotalPages();
    }

    public PageVO() {
    }

    /**
     * 转换成PageVo 保持原对象不变
     *
     * @param page
     * @param <S>
     * @return
     */
    public static <S> PageVO<S> map(Object page) {
        try {
            Class<?> pageImplClass = Try.of(() -> Class.forName("org.springframework.data.domain.PageImpl")).getOrNull();
            Class<?> pageClass = Try.of(() -> Class.forName("com.baomidou.mybatisplus.extension.plugins.pagination.Page")).getOrNull();
            if (pageImplClass != null && pageImplClass.isAssignableFrom(page.getClass())) {
                return PageSpringDataVO.map(page);
            } else if (pageClass != null && pageClass.isAssignableFrom(page.getClass())) {
                return PageBaomidouVO.map(page);
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
    public static <S, T> PageVO<T> map(Object page, Function<S, T> mapper, Class<S> clazz) {
        try {
            Class<?> pageImplClass = Try.of(() -> Class.forName("org.springframework.data.domain.PageImpl")).getOrNull();
            Class<?> pageClass = Try.of(() -> Class.forName("com.baomidou.mybatisplus.extension.plugins.pagination.Page")).getOrNull();
            if (pageImplClass != null && pageImplClass.isAssignableFrom(page.getClass())) {
                return PageSpringDataVO.map(page, mapper, clazz);
            } else if (pageClass != null && pageClass.isAssignableFrom(page.getClass())) {
                return PageBaomidouVO.map(page, mapper, clazz);
            }
            throw new RuntimeException("未找到jpa或者mybatis-plus的分页类");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
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
