package com.vonchange.jdbc.abstractjdbc.model;

import java.util.List;

public class EntityInsertResult {
    private String keySql;
    private String valueSql;
    private String updateStr;
    private List<Object> valueList;

    public EntityInsertResult(String keySql, String valueSql, String updateStr,List<Object> valueList) {
        this.keySql = keySql;
        this.valueSql = valueSql;
        this.updateStr=updateStr;
        this.valueList = valueList;
    }

    public String getUpdateStr() {
        return updateStr;
    }

    public void setUpdateStr(String updateStr) {
        this.updateStr = updateStr;
    }

    public String getValueSql() {
        return valueSql;
    }

    public void setValueSql(String valueSql) {
        this.valueSql = valueSql;
    }

    public List<Object> getValueList() {
        return valueList;
    }

    public void setValueList(List<Object> valueList) {
        this.valueList = valueList;
    }

    public String getKeySql() {
        return keySql;
    }

    public void setKeySql(String keySql) {
        this.keySql = keySql;
    }
}
