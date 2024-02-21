package com.vonchange.jdbc.abstractjdbc.core;

import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;

import java.util.Map;

public class QueryBuilder {
    private DataSourceWrapper dataSourceWrapper;
    private String sqlId;
    private Map<String,Object> param;


    public DataSourceWrapper getDataSourceWrapper() {
        return dataSourceWrapper;
    }

    public QueryBuilder setDataSourceWrapper(DataSourceWrapper dataSourceWrapper) {
        this.dataSourceWrapper = dataSourceWrapper;
        return this;
    }

    public QueryBuilder sqlId(String sqlId) {
        this.sqlId = sqlId;
        return this;
    }


}
