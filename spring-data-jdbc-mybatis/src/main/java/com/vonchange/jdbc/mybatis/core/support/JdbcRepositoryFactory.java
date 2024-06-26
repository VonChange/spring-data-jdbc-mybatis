/*
 * Copyright 2017-2020 the original author or authors.
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


import com.vonchange.common.util.MarkdownUtil;
import com.vonchange.jdbc.client.CrudClient;
import com.vonchange.jdbc.config.ConstantJdbc;
import com.vonchange.jdbc.core.CrudUtil;
import com.vonchange.jdbc.mybatis.core.config.ConfigInfo;
import com.vonchange.jdbc.mybatis.core.config.JdbcConfiguration;
import com.vonchange.jdbc.mybatis.core.query.DataSourceKey;
import com.vonchange.jdbc.util.EntityUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;

import java.util.Optional;

/**
 * Creates repository implementation based on JDBC.
 *
 * @author Jens Schauder
 * @author Greg Turnquist
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public class JdbcRepositoryFactory extends RepositoryFactorySupport {


	private final JdbcConfiguration jdbcConfiguration;



	public JdbcRepositoryFactory(@Qualifier("jdbcConfiguration")JdbcConfiguration jdbcConfiguration) {
		this.jdbcConfiguration = jdbcConfiguration;
	}


	@Override
	public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> aClass) {
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getTargetRepository(org.springframework.data.repository.core.RepositoryInformation)
	 */
	@Override
	protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
		Class<?> domainType =repositoryInformation.getDomainType();
		if(!domainType.equals(QueryModel.class)){//QueryRepository
			EntityUtil.getEntityInfo(domainType);
		}
		String mdFile= CrudUtil.interfaceNameMd(repositoryInformation.getRepositoryInterface());
		//初始化markdown数据
		if(null!=mdFile){
			MarkdownUtil.readMarkdownFile(mdFile,false);
		}
		DataSourceKey dataSourceKey=repositoryInformation.getRepositoryInterface().getAnnotation(DataSourceKey.class);
		String dataSourceKeyValue=null!=dataSourceKey?dataSourceKey.value():ConstantJdbc.DataSourceDefault;
		ConfigInfo configInfo= new ConfigInfo();
		configInfo.setDomainType(domainType);
		CrudClient crudClient= jdbcConfiguration.getCrudClient(dataSourceKeyValue);
		return new SimpleJdbcRepository<>(crudClient,configInfo);
	}


	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getRepositoryBaseClass(org.springframework.data.repository.core.RepositoryMetadata)
	 */
	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata repositoryMetadata) {
		return SimpleJdbcRepository.class;
	}

	@Override
	protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
		if (key != null //
				&& key != QueryLookupStrategy.Key.USE_DECLARED_QUERY //
				&& key != QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND //
		) {
			throw new IllegalArgumentException(String.format("Unsupported query lookup strategy %s!", key));
		}
		return  Optional.of(new JdbcQueryLookupStrategy(jdbcConfiguration));
	}

}
