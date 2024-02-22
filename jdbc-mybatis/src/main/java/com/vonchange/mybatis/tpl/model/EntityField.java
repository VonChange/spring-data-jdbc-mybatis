package com.vonchange.mybatis.tpl.model;

/**
 * 实体字段信息
 * 
 * @author vonchange@163.com
 */
public class EntityField {
	private String fieldName;
	private String columnName;
	private Class<?> type;
	private Boolean isColumn = false;
	private Boolean isId = false;

	private Boolean updateNot=false;
	private Boolean insertNot=false;

	private Boolean version=false;

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


	public Boolean getIsId() {
		return isId;
	}

	public void setIsId(Boolean isId) {
		this.isId = isId;
	}

	public Boolean getIsColumn() {
		return isColumn;
	}

	public void setIsColumn(Boolean isColumn) {
		this.isColumn = isColumn;
	}

	public Boolean getVersion() {
		return version;
	}

	public void setVersion(Boolean version) {
		this.version = version;
	}

	public Boolean getUpdateNot() {
		return updateNot;
	}

	public void setUpdateNot(Boolean updateNot) {
		this.updateNot = updateNot;
	}

	public Boolean getInsertNot() {
		return insertNot;
	}

	public void setInsertNot(Boolean insertNot) {
		this.insertNot = insertNot;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
}
