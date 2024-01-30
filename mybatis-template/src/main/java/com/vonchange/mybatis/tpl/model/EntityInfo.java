package com.vonchange.mybatis.tpl.model;

import java.util.List;

/**
 *  实体信息
 * @author vonchange@163.com
 */
public class EntityInfo {
	private String entityName;
	private String tableName;
	private String idFieldName;
	private String idColumnName;
	private String idType;
	private  List<String> columnReturns;
	private String genColumn;
	private List<EntityField> entityFields;

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getIdFieldName() {
		return idFieldName;
	}



	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public List<EntityField> getEntityFields() {
		return entityFields;
	}

	public void setEntityFields(List<EntityField> entityFields) {
		this.entityFields = entityFields;
	}

	public List<String> getColumnReturns() {
		return columnReturns;
	}

	public void setColumnReturns(List<String> columnReturns) {
		this.columnReturns = columnReturns;
	}

	public String getGenColumn() {
		return genColumn;
	}

	public void setGenColumn(String genColumn) {
		this.genColumn = genColumn;
	}
}
