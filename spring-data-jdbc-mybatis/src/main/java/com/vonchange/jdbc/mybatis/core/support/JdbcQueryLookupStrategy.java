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

import com.vonchange.jdbc.abstractjdbc.config.ConstantJdbc;
import com.vonchange.jdbc.abstractjdbc.core.CrudClient;
import com.vonchange.jdbc.mybatis.core.config.ConfigInfo;
import com.vonchange.jdbc.mybatis.core.config.JdbcConfiguration;
import com.vonchange.jdbc.mybatis.core.query.DataSourceKey;
import com.vonchange.jdbc.mybatis.core.util.JdbcMybatisUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

import java.lang.reflect.Method;

/**
 * {@link QueryLookupStrategy} for JDBC repositories. Currently only supports annotated queries.
 *
 * @author Jens Schauder
 * @author Kazuki Shimizu
 * @author Oliver Gierke
 * @author Mark Paluch
 * @author Maciej Walkowiak
 */
class JdbcQueryLookupStrategy implements QueryLookupStrategy {

	private final JdbcConfiguration jdbcConfiguration;
	//private final DataSourceWrapperHelper dataSourceWrapperHelper;

	/**
	 * Creates a new {@link JdbcQueryLookupStrategy} for the given
	 *
	 */
	JdbcQueryLookupStrategy(@Qualifier("jdbcConfiguration")JdbcConfiguration jdbcConfiguration) {
		this.jdbcConfiguration = jdbcConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.QueryLookupStrategy#resolveQuery(java.lang.reflect.Method, org.springframework.data.repository.core.RepositoryMetadata, org.springframework.data.projection.ProjectionFactory, org.springframework.data.repository.core.NamedQueries)
	 */
	@Override
	public RepositoryQuery resolveQuery(Method method, RepositoryMetadata repositoryMetadata,
			ProjectionFactory projectionFactory, NamedQueries namedQueries) {
		String configLoc = JdbcMybatisUtil.interfaceNameMd(repositoryMetadata.getRepositoryInterface());
		DataSourceKey dataSourceKey=	repositoryMetadata.getRepositoryInterface().getAnnotation(DataSourceKey.class);
		String dataSourceKeyValue=null!=dataSourceKey?dataSourceKey.value(): ConstantJdbc.DataSourceDefault;
		JdbcQueryMethod queryMethod = new JdbcQueryMethod(method, repositoryMetadata, projectionFactory);
		ConfigInfo configInfo= new ConfigInfo();
		configInfo.setMethod(method.getName());
		configInfo.setLocation(configLoc);
		configInfo.setDomainType(repositoryMetadata.getDomainType());
		CrudClient crudClient= jdbcConfiguration.getCrudClient(dataSourceKeyValue);
		return new JdbcRepositoryQuery(queryMethod, crudClient,configInfo);
	}


}
