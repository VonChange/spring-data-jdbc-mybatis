package com.vonchange.jdbc.abstractjdbc.model;

import com.vonchange.mybatis.tpl.model.EntityField;

public class EntityCu {
    private EntityField entityField;
    private Object value;
    private Boolean duplicate;
    private Boolean nullUpdate;
    private String insertIfNullValue;
    private Boolean insertKeyColumn;
    private Boolean insertValueParam;
    private String updateIfNullValue;
    private Boolean updateValueColumn;
    private Boolean updateValueParam;
    public EntityCu() {
    }
    public EntityCu(EntityField entityField, Object value,Boolean duplicate,Boolean nullUpdate) {
        this.entityField = entityField;
        this.value = value;
        this.duplicate=duplicate;
        this.nullUpdate =nullUpdate;
    }

    public Boolean getUpdateValueParam() {
        return updateValueParam;
    }

    public void setUpdateValueParam(Boolean updateValueParam) {
        this.updateValueParam = updateValueParam;
    }

    public Boolean getInsertValueParam() {
        return insertValueParam;
    }

    public void setInsertValueParam(Boolean insertValueParam) {
        this.insertValueParam = insertValueParam;
    }

    public String getUpdateIfNullValue() {
        return updateIfNullValue;
    }

    public void setUpdateIfNullValue(String updateIfNullValue) {
        this.updateIfNullValue = updateIfNullValue;
    }

    public Boolean getUpdateValueColumn() {
        return updateValueColumn;
    }

    public void setUpdateValueColumn(Boolean updateValueColumn) {
        this.updateValueColumn = updateValueColumn;
    }

    public Boolean getInsertKeyColumn() {
        return insertKeyColumn;
    }

    public void setInsertKeyColumn(Boolean insertKeyColumn) {
        this.insertKeyColumn = insertKeyColumn;
    }

    public Boolean getNullUpdate() {
        return nullUpdate;
    }

    public void setNullUpdate(Boolean nullUpdate) {
        this.nullUpdate = nullUpdate;
    }

    public String getInsertIfNullValue() {
        return insertIfNullValue;
    }

    public void setInsertIfNullValue(String insertIfNullValue) {
        this.insertIfNullValue = insertIfNullValue;
    }

    public Boolean getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        this.duplicate = duplicate;
    }


    public EntityField getEntityField() {
        return entityField;
    }

    public void setEntityField(EntityField entityField) {
        this.entityField = entityField;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
