package com.vonchange.jdbc.mybatis.core.config;

import org.springframework.data.domain.Pageable;

import java.util.Map;

public class BindParameterWrapper<T> {
    private Pageable pageable;
    private Map<String, Object> parameter;
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

    public Map<String, Object> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, Object> parameter) {
        this.parameter = parameter;
    }

}
