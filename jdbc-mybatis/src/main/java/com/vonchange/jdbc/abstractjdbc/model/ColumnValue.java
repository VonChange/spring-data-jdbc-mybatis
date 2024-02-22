package com.vonchange.jdbc.abstractjdbc.model;

public class ColumnValue {
    private String column;
    private String param;
    private Object value;
    public ColumnValue(){}
    public ColumnValue(String column, String param, Object value) {
        this.column = column;
        this.param = param;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public String getParam() {
        return param;
    }

    public Object getValue() {
        return value;
    }
}
