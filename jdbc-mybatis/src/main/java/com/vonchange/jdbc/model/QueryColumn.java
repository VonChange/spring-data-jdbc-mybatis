package com.vonchange.jdbc.model;

public class QueryColumn {

    private String join;
    private String column;
    private String condition;
    private Object value;
    public QueryColumn(){

    }
    public QueryColumn(String column, String condition, Object value) {
        this.column = column;
        this.condition = condition;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public String getJoin() {
        return join;
    }

    public void setJoin(String join) {
        this.join = join;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
