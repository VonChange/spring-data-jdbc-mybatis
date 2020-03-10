package org.springframework.data.mybatis.mini.jdbc.repository.config;

import com.vonchange.jdbc.abstractjdbc.handler.AbstractPageWork;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public class BindParameterWrapper {
    private  Pageable pageable;
    private  AbstractPageWork abstractPageWork;
    private Map<String,Object> parameter;

    public AbstractPageWork getAbstractPageWork() {
        return abstractPageWork;
    }

    public void setAbstractPageWork(AbstractPageWork abstractPageWork) {
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
}
