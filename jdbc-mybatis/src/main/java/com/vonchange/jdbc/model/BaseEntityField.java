package com.vonchange.jdbc.model;

public class BaseEntityField {
    private String fieldName;
    private String columnName;
    private Class<?> type;
    private Boolean ifColumn = true;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Boolean getIfColumn() {
        return ifColumn;
    }

    public void setIfColumn(Boolean ifColumn) {
        this.ifColumn = ifColumn;
    }
}
