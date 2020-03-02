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
package org.springframework.data.mybatis.mini.jdbc.repository.support;

import com.vonchange.jdbc.abstractjdbc.core.JdbcRepostitory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mybatis.mini.jdbc.repository.config.ConfigInfo;
import org.springframework.data.mybatis.mini.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * A query to be executed based on a repository method, it's annotated SQL query and the arguments provided to the
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
	private final JdbcRepostitory operations;
	private final ConfigInfo configInfo;

	/**
	 * Creates a new {@link JdbcRepositoryQuery} for the given {@link JdbcQueryMethod}, {@link RelationalMappingContext} and
	 * {@link RowMapper}.
	 *
	 * @param queryMethod must not be {@literal null}.
	 * @param operations must not be {@literal null}.
	 */
	JdbcRepositoryQuery(JdbcQueryMethod queryMethod, JdbcRepostitory operations, ConfigInfo configInfo) {

		Assert.notNull(queryMethod, "Query method must not be null!");
		Assert.notNull(operations, "NamedParameterJdbcOperations must not be null!");
		Assert.notNull(configInfo, "configLocation must not be null!");
		this.queryMethod = queryMethod;
		this.operations = operations;
		this.configInfo=configInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.RepositoryQuery#execute(java.lang.Object[])
	 */
	@Override
	public Object execute(Object[] objects) {
		Map<String,Object> parameters = bindParameter(objects);
        String sqlId= configInfo.getLocation()+"."+configInfo.getMethod();
		if (configInfo.getMethod().startsWith("update")) {
			int updatedCount = operations.update(sqlId,parameters);
			Class<?> returnedObjectType = queryMethod.getReturnedObjectType();
			return (returnedObjectType == boolean.class || returnedObjectType == Boolean.class) ? updatedCount != 0
					: updatedCount;
		}
		if (configInfo.getMethod().startsWith("insert")||configInfo.getMethod().startsWith("save")) {
			return operations.insert(sqlId,parameters);
		}

		if (queryMethod.isCollectionQuery() || queryMethod.isStreamQuery()) {

			return operations.queryList(configInfo.getType(), sqlId,parameters);
		}

		try {
			return operations.queryOne(configInfo.getType(),sqlId,parameters);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.RepositoryQuery#getQueryMethod()
	 */
	@Override
	public JdbcQueryMethod getQueryMethod() {
		return queryMethod;
	}


	private Map<String,Object> bindParameter(Object[] objects) {
		Map<String,Object> map = new HashMap<>();
		queryMethod.getParameters().getBindableParameters().forEach(p -> {
			String parameterName = p.getName().orElseThrow(() -> new IllegalStateException(PARAMETER_NEEDS_TO_BE_NAMED));
			map.put(parameterName, objects[p.getIndex()]);
		});
		return map;
	}







}
