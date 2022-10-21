package com.vonchange.jdbc.abstractjdbc.model;

import java.util.List;

/**
 * sql片段实体
 * @author von_change@163.com
 * 2015-6-14 下午12:45:34
 */
public class SqlFragment {
	private String sql;
	private List<Object> params;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

}
