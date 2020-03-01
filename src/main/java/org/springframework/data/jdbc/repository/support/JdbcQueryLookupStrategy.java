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
package org.springframework.data.jdbc.repository.support;

import com.vonchange.jdbc.abstractjdbc.core.JdbcRepostitory;
import org.springframework.data.jdbc.repository.config.ConfigInfo;
import org.springframework.data.jdbc.repository.query.ConfigLocation;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
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

	private final JdbcRepostitory operations;

	/**
	 * Creates a new {@link JdbcQueryLookupStrategy} for the given {@link RelationalMappingContext},
	 *
	 */
	JdbcQueryLookupStrategy(JdbcRepostitory operations) {

		Assert.notNull(operations, "operations must not be null!");
		this.operations = operations;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.QueryLookupStrategy#resolveQuery(java.lang.reflect.Method, org.springframework.data.repository.core.RepositoryMetadata, org.springframework.data.projection.ProjectionFactory, org.springframework.data.repository.core.NamedQueries)
	 */
	@Override
	public RepositoryQuery resolveQuery(Method method, RepositoryMetadata repositoryMetadata,
			ProjectionFactory projectionFactory, NamedQueries namedQueries) {

		ConfigLocation configLocation=	repositoryMetadata.getRepositoryInterface().getAnnotation(ConfigLocation.class);
		String configLoc=null!=configLocation?configLocation.value():"sql.sql";
		JdbcQueryMethod queryMethod = new JdbcQueryMethod(method, repositoryMetadata, projectionFactory);

		ConfigInfo configInfo= new ConfigInfo();
		configInfo.setMethod(method.getName());
		configInfo.setLocation(configLoc);
		configInfo.setType(repositoryMetadata.getDomainType());
		return new JdbcRepositoryQuery(queryMethod, operations,configInfo);
	}

}
