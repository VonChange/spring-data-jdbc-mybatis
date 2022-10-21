package com.vonchange.mybatis.tpl.extra;

import java.util.List;

public class SqlParamResult {
    private List<String> param;
    private String newSql;

    public SqlParamResult(List<String> param, String newSql) {
        this.param = param;
        this.newSql = newSql;
    }

    public List<String> getParam() {
        return param;
    }

    public void setParam(List<String> param) {
        this.param = param;
    }

    public String getNewSql() {
        return newSql;
    }

    public void setNewSql(String newSql) {
        this.newSql = newSql;
    }
}
