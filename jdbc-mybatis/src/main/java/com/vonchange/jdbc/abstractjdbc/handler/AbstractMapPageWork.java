package com.vonchange.jdbc.abstractjdbc.handler;

import java.util.List;
import java.util.Map;

/**
 * Created by 冯昌义 on 2018/4/17.
 */
public abstract class AbstractMapPageWork {
    private long totalElements;
    private int totalPages;
    private int size;
    protected abstract  void doPage(List<Map<String,Object>> pageContentList, int pageNum, Map<String,Object> extData);
    protected abstract  int  getPageSize();

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
