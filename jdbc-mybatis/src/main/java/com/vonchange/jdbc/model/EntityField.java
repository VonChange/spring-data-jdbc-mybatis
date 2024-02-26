package com.vonchange.jdbc.model;

/**
 * 实体字段信息
 * 
 * @author vonchange@163.com
 */
public class EntityField extends BaseEntityField{

	private Boolean ifId = false;

	private Boolean updateNot=false;
	private Boolean insertNot=false;

	private Boolean version=false;


	public Boolean getIfId() {
		return ifId;
	}

	public void setIfId(Boolean ifId) {
		this.ifId = ifId;
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

}
