package com.vonchange.jdbc.mybatis.core.config;

import com.vonchange.jdbc.abstractjdbc.handler.AbstractPageWork;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public class BindParameterWrapper<T> {
    private Pageable pageable;
    private AbstractPageWork<T> abstractPageWork;
    private Class<? extends T> abstractPageWorkClass;
    private Map<String, Object> parameter;
    private Object firstParam;

    public Object getFirstParam() {
        return firstParam;
    }

    public void setFirstParam(Object firstParam) {
        this.firstParam = firstParam;
    }

    public AbstractPageWork<T> getAbstractPageWork() {
        return abstractPageWork;
    }

    public void setAbstractPageWork(AbstractPageWork<T> abstractPageWork) {
        this.abstractPageWork = abstractPageWork;
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

    public Class<? extends T> getAbstractPageWorkClass() {
        return abstractPageWorkClass;
    }

    public void setAbstractPageWorkClass(Class<? extends T> abstractPageWorkClass) {
        this.abstractPageWorkClass = abstractPageWorkClass;
    }
}
