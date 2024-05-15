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

import com.vonchange.jdbc.mybatis.core.config.JdbcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;

import java.io.Serializable;

//import org.springframework.data.jdbc.core.DefaultDataAccessStrategy;

/**
 * Special adapter for Springs
 * {@link org.springframework.beans.factory.FactoryBean} interface to allow easy
 * setup of
 * repository factories via Spring configuration.
 *
 * @author Jens Schauder
 * @author Greg Turnquist
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @author Mark Paluch
 */
public class JdbcRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> //
		extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> {

	private JdbcConfiguration jdbcConfiguration;

	/**
	 * Creates a new {@link JdbcRepositoryFactoryBean} for the given repository
	 * interface.
	 *
	 * @param repositoryInterface must not be {@literal null}.
	 */
	JdbcRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport
	 * #setApplicationEventPublisher(org.springframework.context.
	 * ApplicationEventPublisher)
	 */
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {

		super.setApplicationEventPublisher(publisher);
	}

	/**
	 * Creates the actual {@link RepositoryFactorySupport} instance.
	 */
	@Override
	protected RepositoryFactorySupport doCreateRepositoryFactory() {
		return new JdbcRepositoryFactory(jdbcConfiguration);
	}



	@Autowired
	public void setJdbcOperations(@Qualifier("jdbcConfiguration") JdbcConfiguration jdbcConfiguration) {
		this.jdbcConfiguration = jdbcConfiguration;
	}

}
