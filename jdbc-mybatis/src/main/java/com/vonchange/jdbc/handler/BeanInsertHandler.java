/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vonchange.jdbc.handler;


import com.vonchange.common.util.UtilAll;
import com.vonchange.jdbc.core.CrudUtil;
import com.vonchange.jdbc.util.ConvertMap;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.tpl.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <core>ResultSetHandler</core> implementation that converts the first
 * <core>ResultSet</core> row into a JavaBean. This class is thread safe.
 *
 * @param <T>
 *            the target bean type
 */
public class BeanInsertHandler<T> implements ResultSetExtractor<T> {

	private static final Logger log = LoggerFactory.getLogger(BeanInsertHandler.class);
	/**
	 * The Class of beans produced by this handler.
	 */
	private final T entity;


	public BeanInsertHandler(T entity) {
		this.entity = entity;
	}

	/**
	 * Convert the first row of the <core>ResultSet</core> into a bean with the
	 * <core>Class</core> given in the constructor.
	 *
	 * @param rs
	 *            <core>ResultSet</core> to process.
	 * @return An initialized JavaBean or <core>null</core> if there were no
	 *         rows in the <core>ResultSet</core>.
	 *
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	@Override
	public T extractData(ResultSet rs) throws SQLException {
		return rs.next() ? this.toBean(rs, this.entity) : null;
	}

	private T toBean(ResultSet rs, T entity) throws SQLException {
		String idColumnName = EntityUtil.getEntityInfo(entity.getClass()).getIdColumnName();
		if(UtilAll.UString.isBlank(idColumnName)){
			throw new JdbcMybatisRuntimeException("need @Id in your entity");
		}
		ConvertMap.toBean(CrudUtil.rowToMap(rs,idColumnName),entity);
		return entity;
	}
}
