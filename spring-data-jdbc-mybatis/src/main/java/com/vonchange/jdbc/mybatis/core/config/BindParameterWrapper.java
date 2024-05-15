package com.vonchange.jdbc.mybatis.core.config;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public class BindParameterWrapper<T> {
    private Pageable pageable;
    private Map<String, Object> namedParams;
    private List<Object> indexedParams;
    private Object firstParam;

    public Object getFirstParam() {
        return firstParam;
    }

    public void setFirstParam(Object firstParam) {
        this.firstParam = firstParam;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public Map<String, Object> getNamedParams() {
        return namedParams;
    }

    public void setNamedParams(Map<String, Object> namedParams) {
        this.namedParams = namedParams;
    }

    public List<Object> getIndexedParams() {
        return indexedParams;
    }

    public void setIndexedParams(List<Object> indexedParams) {
        this.indexedParams = indexedParams;
    }
}
