package com.gymmanage.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public class PageResult<T> {
    private List<T> items;
    private long total;
    private long page;
    private long pageSize;

    public static <T> PageResult<T> from(Page<T> p) {
        PageResult<T> r = new PageResult<>();
        r.setItems(p.getRecords());
        r.setTotal(p.getTotal());
        r.setPage(p.getCurrent());
        r.setPageSize(p.getSize());
        return r;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
}

