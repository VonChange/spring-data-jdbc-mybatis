package com.vonchange.jdbc.model;

import com.vonchange.mybatis.dialect.Dialect;

import javax.sql.DataSource;

public class DataSourceWrapper {


    private DataSource dataSource;
    private String key;
    private Dialect dialect;
    public DataSourceWrapper(DataSource dataSource,String key,Dialect dialect){
        this.dataSource=dataSource;
        this.key=key;
        this.dialect=dialect;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public DataSource getDataSource() {
        return dataSource;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public DataSourceWrapper setDialect(Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    public String getKey() {
        return key;
    }

}
