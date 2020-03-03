package org.springframework.data.mybatis.mini.jdbc.repository.config;

import org.springframework.data.domain.Pageable;

import java.util.Map;

public class BindParameterWrapper {
    private  Pageable pageable;
    private Map<String,Object> parameter;

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
