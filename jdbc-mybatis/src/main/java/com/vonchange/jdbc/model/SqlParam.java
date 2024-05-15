package com.vonchange.jdbc.model;

import com.vonchange.jdbc.config.EnumSqlRead;

import java.util.Collection;
import java.util.List;

/**
 * @author 冯昌义
 */
public class SqlParam{
    private String sql;
    private Collection<?> params;
    private List<String> propertyNames;
    private  List<String> columnReturns;

    private Boolean version=false;
    private EnumSqlRead sqlRead;

    public SqlParam(String sql, Collection<?> params) {
        this.sql = sql;
        this.params = params;
    }
    public String getSql() {
        return sql;
    }

    public Collection<?> getParams() {
        return params;
    }
    public EnumSqlRead getSqlRead() {
        return sqlRead;
    }

    public void setSqlRead(EnumSqlRead sqlRead) {
        this.sqlRead = sqlRead;
    }

    public Boolean getVersion() {
        return version;
    }

    public void setVersion(Boolean version) {
        this.version = version;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(List<String> propertyNames) {
        this.propertyNames = propertyNames;
    }


    public List<String> getColumnReturns() {
        return columnReturns;
    }

    public void setColumnReturns(List<String> columnReturns) {
        this.columnReturns = columnReturns;
    }

}
