package com.vonchange.mybatis.tpl.model;

import java.util.Arrays;
import java.util.List;

/**
 * @author 冯昌义
 */
public class SqlWithParam {
    private String sql;
    private Object[] params;
    private List<String> propertyNames;
    private  List<String> columnReturns;

    private String idFieldName;
    private long total;
    public SqlWithParam(){
    }
    public SqlWithParam(String sql, Object[] params) {
        this.sql = sql;
        this.params = params;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getSql() {
        return sql;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(List<String> propertyNames) {
        this.propertyNames = propertyNames;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public List<String> getColumnReturns() {
        return columnReturns;
    }

    public void setColumnReturns(List<String> columnReturns) {
        this.columnReturns = columnReturns;
    }

    public String getIdFieldName() {
        return idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    @Override
    public String toString() {
        return "SqlWithParam{" +
                "sql='" + sql + '\'' +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
