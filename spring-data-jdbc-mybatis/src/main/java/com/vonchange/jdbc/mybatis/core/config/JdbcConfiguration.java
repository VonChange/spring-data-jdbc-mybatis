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
package com.vonchange.jdbc.mybatis.core.config;

import com.vonchange.jdbc.mybatis.repository.JdbcRepositorySpringImpl;

import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Beans that must be registered for Spring Data JDBC to work.
 *
 * @author Greg Turnquist
 * @author Jens Schauder
 * @author Mark Paluch
 * @author Michael Simons
 * @author Christoph Strobl
 */
@Configuration
public class JdbcConfiguration {
	@Bean(name = "jdbcRepository")
	public JdbcRepository initJdbcRepository(DataSource... dataSource){
		return new JdbcRepositorySpringImpl(dataSource);
	}
	@Bean
	public DataSourceWrapperHelper initDataSourceWrapperHelper(@Autowired(required = false)DataSourceWrapper... dataSourceWrapper){
		return new DataSourceWrapperHelperImpl(dataSourceWrapper);
	}
}
