package com.vonchange.jdbc.model;

import java.util.Collection;

public class BaseSqlParam {
    private String sql;
    private Collection<?> params;

    public BaseSqlParam(String sql, Collection<?> params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public Collection<?> getParams() {
        return params;
    }
}
