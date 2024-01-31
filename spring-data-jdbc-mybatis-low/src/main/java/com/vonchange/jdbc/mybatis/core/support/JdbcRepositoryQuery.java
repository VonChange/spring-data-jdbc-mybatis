/*
 * Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vonchange.jdbc.mybatis.core.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vonchange.jdbc.mybatis.core.config.BindParameterWrapper;
import com.vonchange.jdbc.mybatis.core.config.ConfigInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.vonchange.common.util.ClazzUtils;
import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import com.vonchange.jdbc.abstractjdbc.handler.AbstractPageWork;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;

/**
 * A query to be executed based on a repository method, it's annotated SQL query
 * and the arguments provided to the
 * method.
 *
 * @author Jens Schauder
 * @author Kazuki Shimizu
 * @author Oliver Gierke
 * @author Maciej Walkowiak
 */

class JdbcRepositoryQuery implements RepositoryQuery {
	private static final String PARAMETER_NEEDS_TO_BE_NAMED = "For queries with named parameters you need to provide names for method parameters. Use @Param for query method parameters, or when on Java 8+ use the javac flag -parameters.";

	private final JdbcQueryMethod queryMethod;
	private final JdbcRepository operations;
	private final ConfigInfo configInfo;

	/**
	 * Creates a new {@link JdbcRepositoryQuery} for the given
	 * {@link JdbcQueryMethod}, and
	 * {@link RowMapper}.
	 *
	 * @param queryMethod must not be {@literal null}.
	 * @param operations  must not be {@literal null}.
	 */
	JdbcRepositoryQuery(JdbcQueryMethod queryMethod, @Qualifier("jdbcRepository") JdbcRepository operations,
			ConfigInfo configInfo) {

		Assert.notNull(queryMethod, "Query method must not be null!");
		Assert.notNull(operations, "NamedParameterJdbcOperations must not be null!");
		Assert.notNull(configInfo, "configLocation must not be null!");
		this.queryMethod = queryMethod;
		this.operations = operations;
		this.configInfo = configInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.query.RepositoryQuery#execute(java.lang.
	 * Object[])
	 */
	@Override
	public Object execute(Object[] objects) {
		return executeDo(objects);
	}

	@SuppressWarnings("unchecked")
	private Object executeDo(Object[] objects) {
		BindParameterWrapper parameters = bindParameter(objects);
		String sqlId = configInfo.getLocation() + "." + configInfo.getMethod();
		DataSourceWrapper dataSourceWrapper = configInfo.getDataSourceWrapper();
		if (null == dataSourceWrapper && queryMethod.isReadDataSource()) {
			dataSourceWrapper = operations.getReadDataSource();
		}
		if (queryMethod.isBatchUpdate()) {
			return operations.batchUpdate(dataSourceWrapper, sqlId, (List<Object>) parameters.getFirstParam(),
					queryMethod.getBatchSize());
		}
		if (configInfo.getMethod().startsWith("update") || configInfo.getMethod().startsWith("delete")) {
			int updatedCount = operations.update(dataSourceWrapper, sqlId, parameters.getParameter());
			Class<?> returnedObjectType = queryMethod.getReturnedObjectType();
			return (returnedObjectType == boolean.class || returnedObjectType == Boolean.class) ? updatedCount != 0
					: updatedCount;
		}
		if (configInfo.getMethod().startsWith("insert") || configInfo.getMethod().startsWith("save")) {
			return operations.insert(dataSourceWrapper, sqlId, parameters.getParameter());
		}
		if (null != parameters.getAbstractPageWork()) {
			operations.queryBigData(dataSourceWrapper, parameters.getAbstractPageWorkClass(), sqlId,
					parameters.getAbstractPageWork(), parameters.getParameter());
		}
		if (queryMethod.isCollectionQuery() || queryMethod.isStreamQuery()) {
			return operations.queryList(dataSourceWrapper, queryMethod.getReturnedObjectType(), sqlId,
					parameters.getParameter());
		}
		if (queryMethod.isPageQuery()) {
			return operations.queryPage(dataSourceWrapper, queryMethod.getReturnedObjectType(), sqlId,
					parameters.getPageable(), parameters.getParameter());
		}

		if (ClazzUtils.isBaseType(queryMethod.getReturnedObjectType())) {
			return operations.queryOneColumn(dataSourceWrapper, queryMethod.getReturnedObjectType(), sqlId,
					parameters.getParameter());
		}
		try {
			return operations.queryOne(dataSourceWrapper, queryMethod.getReturnedObjectType(), sqlId,
					parameters.getParameter());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.query.RepositoryQuery#getQueryMethod()
	 */
	@Override
	public JdbcQueryMethod getQueryMethod() {
		return queryMethod;
	}

	private BindParameterWrapper bindParameter(Object[] objects) {
		Parameters<?, ?> parameters = queryMethod.getParameters();
		Map<String, Object> map = new HashMap<>();
		BindParameterWrapper bindParameterWrapper = new BindParameterWrapper();
		if (objects.length > 0) {
			if (parameters.getPageableIndex() >= 0) {
				bindParameterWrapper.setPageable((Pageable) objects[parameters.getPageableIndex()]);
			}
			Class<?> type = queryMethod.getParameters().getParameter(0).getType();
			if (ClassUtils.isAssignable(AbstractPageWork.class, type)) {
				bindParameterWrapper.setAbstractPageWork((AbstractPageWork<?>) objects[0]);
				Type superClass = objects[0].getClass().getGenericSuperclass();
				if (superClass instanceof ParameterizedType) {
					ParameterizedType pType = (ParameterizedType) superClass;
					bindParameterWrapper.setAbstractPageWorkClass((Class<?>) pType.getActualTypeArguments()[0]);
				}
			}
			bindParameterWrapper.setFirstParam(objects[0]);
		}
		if (queryMethod.isBatchUpdate()) {
			return bindParameterWrapper;
		}
		for (Parameter p : queryMethod.getParameters().getBindableParameters()) {
			String parameterName = p.getName();
			if (null == parameterName) {
				throw new IllegalStateException(PARAMETER_NEEDS_TO_BE_NAMED);
			}
			map.put(parameterName, objects[p.getIndex()]);
		}
		bindParameterWrapper.setParameter(map);
		return bindParameterWrapper;
	}

}
