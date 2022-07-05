package com.yj2025.commons.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 当前分页对象只存基本的字段
 *
 * @author leiyang
 * @date 2021/7/13 21:12
 */
public class PageRespVo<T> implements Serializable {
    /**
     * 默认当前页
     */
    protected static final int DEFAULT_PAGE_NUM = 0;

    /**
     * 默认一页行数
     */
    protected static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 总页数
     */
    protected long totalPages;

    /**
     * 总数
     */
    protected long totalElements;

    /**
     * 当前页
     */
    protected long number;

    /**
     * 当前页的数量
     */
    protected long size;

    /**
     * 数据
     */
    protected List<T> content;

    /**
     * 是否有下一页
     */
    protected boolean hasNext;


    public PageRespVo(List<T> content, long totalElements, long totalPages, long number, long size) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.number = number;
        this.size = size;
        this.content = content;
        this.hasNext = (this.number + 1) < this.getTotalPages();
    }

    public PageRespVo() {
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isHasNext() {
        return (this.number + 1) < this.getTotalPages();
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = (this.number + 1) < this.getTotalPages();
    }
}
