package com.vonchange.jdbc.model;

import java.util.List;

public class BaseSqlParam {
    private String sql;
    private List<Object> params;

    public BaseSqlParam(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public List<Object> getParams() {
        return params;
    }
}
