package com.vonchange.jdbc.model;

import java.util.List;
import java.util.Map;

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
	private List<EntityField> entityFields;

	private Map<String,Integer> fieldMap;
	private Map<String,Integer> columnMap;
	private EntityField logicDeleteField;

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


	public Map<String, Integer> getColumnMap() {
		return columnMap;
	}

	public void setColumnMap(Map<String, Integer> columnMap) {
		this.columnMap = columnMap;
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

	public Map<String, Integer> getFieldMap() {
		return fieldMap;
	}

	public void setFieldMap(Map<String, Integer> fieldMap) {
		this.fieldMap = fieldMap;
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

	public EntityField getLogicDeleteField() {
		return logicDeleteField;
	}

	public void setLogicDeleteField(EntityField logicDeleteField) {
		this.logicDeleteField = logicDeleteField;
	}
}
