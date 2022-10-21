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
package com.vonchange.jdbc.abstractjdbc.handler;


import com.vonchange.jdbc.abstractjdbc.util.ConvertMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <core>ResultSetHandler</core> implementation that converts the first
 * <core>ResultSet</core> row into a JavaBean. This class is thread safe.
 *
 * @param <T>
 *            the target bean type
 */
public class BeanHandler<T> implements ResultSetExtractor<T> {

	private static final Logger log = LoggerFactory.getLogger(BeanHandler.class);
	/**
	 * The Class of beans produced by this handler.
	 */
	private final Class<? extends T> type;


	public BeanHandler(Class<? extends T> type) {
		this.type = type;
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
		return rs.next() ? this.toBean(rs, this.type) : null;
	}

	private T toBean(ResultSet rs, Class<? extends T> type) throws SQLException {
		T entity = null;
		try {
			entity = ConvertMap.convertMap(type,ConvertMap.newMap(HandlerUtil.rowToMap(rs)));
		} catch (IntrospectionException  | IllegalAccessException | InvocationTargetException e) {
			log.error("exception",e);
		}
		return entity;
	}
}
