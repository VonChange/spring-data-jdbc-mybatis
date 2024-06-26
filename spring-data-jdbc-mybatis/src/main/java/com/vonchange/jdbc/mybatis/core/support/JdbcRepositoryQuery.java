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

import com.vonchange.common.util.ClazzUtils;
import com.vonchange.common.util.MarkdownUtil;
import com.vonchange.common.util.StringPool;
import com.vonchange.jdbc.client.CrudClient;
import com.vonchange.jdbc.config.ConstantJdbc;
import com.vonchange.jdbc.model.SqlParam;
import com.vonchange.jdbc.mybatis.core.config.BindParameterWrapper;
import com.vonchange.jdbc.mybatis.core.config.ConfigInfo;
import com.vonchange.jdbc.util.NameQueryUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final CrudClient operations;
	private final ConfigInfo configInfo;

	/**
	 * Creates a new {@link JdbcRepositoryQuery} for the given
	 * {@link JdbcQueryMethod}, and
	 *
	 * @param queryMethod must not be {@literal null}.
	 * @param operations  must not be {@literal null}.
	 */
	JdbcRepositoryQuery(JdbcQueryMethod queryMethod,  CrudClient operations,
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
	private <T> Object executeDo(Object[] objects) {
		BindParameterWrapper<T> bindParameter = bindParameter(objects);
		String sqlId=configInfo.getMethod();
		boolean nameQuery=true;
		SqlParam sqlParam = null;
		if(null!=configInfo.getLocation()){
			String sqlIdInMd = configInfo.getLocation() + StringPool.DOT + configInfo.getMethod();
			if(null!=MarkdownUtil.getContent(sqlIdInMd,false)){
				sqlId=sqlIdInMd;
				nameQuery =false;
			}
		}
		if(nameQuery){
			Assert.notNull(configInfo.getDomainType(),"domain type must not null,define  crudRepository");
			sqlParam = NameQueryUtil.nameSql(sqlId,configInfo.getDomainType(), bindParameter.getIndexedParams());
		}
		if (queryMethod.isBatchUpdate()) {
			return operations.sqlId(sqlId).updateBatch((List<Object>) bindParameter.getFirstParam());
		}
		if (queryMethod.isUpdateQuery() || configInfo.getMethod().startsWith("update")
				|| configInfo.getMethod().startsWith("delete")||configInfo.getMethod().startsWith("insert")
				|| configInfo.getMethod().startsWith("save")) {
			int updatedCount = operations.sqlId(sqlId).params(bindParameter.getNamedParams()).update();
			Class<?> returnedObjectType = queryMethod.getReturnedObjectType();
			return (returnedObjectType == boolean.class || returnedObjectType == Boolean.class) ? updatedCount != 0
					: updatedCount;
		}
		if (queryMethod.isPageQuery()) {
			return  nameQuery?operations.jdbc().sql(sqlParam.getSql()).params(sqlParam.getParams()).query(queryMethod.getReturnedObjectType()).page(bindParameter.getPageable()):
					operations.sqlId(sqlId).params(bindParameter.getNamedParams())
							.query(queryMethod.getReturnedObjectType()).page(bindParameter.getPageable());
		}
		if (queryMethod.isCollectionQuery() || queryMethod.isStreamQuery()) {
			return nameQuery?operations.jdbc().sql(sqlParam.getSql()).params(sqlParam.getParams()).query(queryMethod.getReturnedObjectType()).list():
					operations.sqlId(sqlId).params(bindParameter.getNamedParams()).query(queryMethod.getReturnedObjectType()).list();
		}
		if (ClazzUtils.isBaseType(queryMethod.getReturnedObjectType())) {
			return nameQuery?operations.jdbc().sql(sqlParam.getSql()).params(sqlParam.getParams()).query(queryMethod.getReturnedObjectType()).single():
					operations.sqlId(sqlId).params(bindParameter.getNamedParams()).query(queryMethod.getReturnedObjectType()).single();
		}


		return nameQuery?operations.jdbc().sql(sqlParam.getSql()).params(sqlParam.getParams()).query(queryMethod.getReturnedObjectType()).single():
				operations.sqlId(sqlId).params(bindParameter.getNamedParams()).query(queryMethod.getReturnedObjectType()).single();

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

	@SuppressWarnings("unchecked")
	private <T> BindParameterWrapper<T> bindParameter(Object[] objects) {
		Parameters<?, ?> parameters = queryMethod.getParameters();
		Map<String, Object> namedParams = new HashMap<>();
		List<Object> indexedParams=new ArrayList<>();
		BindParameterWrapper<T> bindParameterWrapper = new BindParameterWrapper<>();
		if (objects.length > 0) {
			if (parameters.getPageableIndex() >= 0) {
				bindParameterWrapper.setPageable((Pageable) objects[parameters.getPageableIndex()]);
			}
			bindParameterWrapper.setFirstParam(objects[0]);
		}
		if (queryMethod.isBatchUpdate()) {
			return bindParameterWrapper;
		}
		queryMethod.getParameters().getBindableParameters().forEach(p -> {
			String parameterName = p.getName().orElse(ConstantJdbc.MybatisIndexParam+p.getIndex());
					//.orElseThrow(() -> new IllegalStateException(PARAMETER_NEEDS_TO_BE_NAMED));
			namedParams.put(parameterName, objects[p.getIndex()]);
			indexedParams.add(objects[p.getIndex()]);
		});
		bindParameterWrapper.setNamedParams(namedParams);
		bindParameterWrapper.setIndexedParams(indexedParams);
		return bindParameterWrapper;
	}

}
