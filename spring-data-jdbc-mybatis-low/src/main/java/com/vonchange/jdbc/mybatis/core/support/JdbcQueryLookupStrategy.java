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

import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import com.vonchange.jdbc.mybatis.core.config.ConfigInfo;
import com.vonchange.jdbc.mybatis.core.config.DataSourceWrapperHelper;
import com.vonchange.jdbc.mybatis.core.query.DataSourceKey;
import com.vonchange.jdbc.mybatis.core.query.SqlPackage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.Assert;

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

	private final JdbcRepository operations;
	private final DataSourceWrapperHelper dataSourceWrapperHelper;

	/**
	 * Creates a new {@link JdbcQueryLookupStrategy} for the given
	 *
	 */
	JdbcQueryLookupStrategy(@Qualifier("jdbcRepository")JdbcRepository operations, DataSourceWrapperHelper dataSourceWrapperHelper) {

		Assert.notNull(operations, "operations must not be null!");
		this.operations = operations;
		this.dataSourceWrapperHelper=dataSourceWrapperHelper;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.QueryLookupStrategy#resolveQuery(java.lang.reflect.Method, org.springframework.data.repository.core.RepositoryMetadata, org.springframework.data.projection.ProjectionFactory, org.springframework.data.repository.core.NamedQueries)
	 */
	@Override
	public RepositoryQuery resolveQuery(Method method, RepositoryMetadata repositoryMetadata,
			ProjectionFactory projectionFactory, NamedQueries namedQueries) {
        String interfaceName =repositoryMetadata.getRepositoryInterface().getSimpleName();
		SqlPackage sqlPackage=	repositoryMetadata.getRepositoryInterface().getAnnotation(SqlPackage.class);
		DataSourceKey dataSourceKey=	repositoryMetadata.getRepositoryInterface().getAnnotation(DataSourceKey.class);
		String configLoc=null!=sqlPackage?sqlPackage.value()+"."+interfaceName:"sql."+interfaceName;
		String dataSourceKeyValue=null!=dataSourceKey?dataSourceKey.value():null;
		JdbcQueryMethod queryMethod = new JdbcQueryMethod(method, repositoryMetadata, projectionFactory);
		ConfigInfo configInfo= new ConfigInfo();
		configInfo.setMethod(method.getName());
		configInfo.setLocation(configLoc);
		configInfo.setRepositoryName(interfaceName);
		configInfo.setType(repositoryMetadata.getDomainType());
		configInfo.setDataSourceWrapper(null!=dataSourceKeyValue?dataSourceWrapperHelper.getDataSourceWrapperByKey(dataSourceKeyValue):null);
		return new JdbcRepositoryQuery(queryMethod, operations,configInfo);
	}

}
