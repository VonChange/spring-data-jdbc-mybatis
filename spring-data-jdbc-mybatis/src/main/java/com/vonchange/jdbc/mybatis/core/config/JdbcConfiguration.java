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

import com.vonchange.jdbc.abstractjdbc.config.ConstantJdbc;
import com.vonchange.jdbc.abstractjdbc.core.CrudClient;
import com.vonchange.jdbc.abstractjdbc.core.DefaultCrudClient;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.dialect.MySQLDialect;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Beans that must be registered for Spring Data JDBC to work.
 *
 * @author Greg Turnquist
 * @author Jens Schauder
 * @author Mark Paluch
 * @author Michael Simons
 * @author Christoph Strobl
 */
@Configuration("jdbcConfiguration")
public class JdbcConfiguration {

    public static final Map<String, CrudClient> crudClientMap = new ConcurrentHashMap<>();
	private Dialect defaultDialect;
	private DataSource dataSource;

	public CrudClient getCrudClient(String key){
		if(!crudClientMap.containsKey(key)){
			throw new JdbcMybatisRuntimeException("datasource {} not found",key);
		}
		return crudClientMap.get(key);
	}
	@Autowired
	public void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
		this.dataSource = dataSource;
	}
	@Autowired
	public void setDefaultDialect(@Autowired(required = false) Dialect dialect) {
		if(null==dialect){
			defaultDialect=new MySQLDialect();
			return;
		}
		this.defaultDialect=dialect;
	}
	@Autowired
	public void setDataSourceWrappers(@Autowired(required = false)DataSourceWrapper... dataSourceWrapper) {
		for (DataSourceWrapper sourceWrapper : dataSourceWrapper) {
			if(null==sourceWrapper.getDialect()){
				sourceWrapper.setDialect(new MySQLDialect());
			}
			crudClientMap.put(sourceWrapper.getKey(),
					new DefaultCrudClient(sourceWrapper));
		}
		if(!crudClientMap.containsKey(ConstantJdbc.DataSourceDefault)){
			DataSourceWrapper defaultDataSourceWrapper=defaultDataSource();
			crudClientMap.put(defaultDataSourceWrapper.getKey(),
					new DefaultCrudClient(defaultDataSourceWrapper));
		}
	}
	private DataSourceWrapper defaultDataSource() {
		return new DataSourceWrapper(dataSource, ConstantJdbc.DataSourceDefault,this.defaultDialect);
	}

}
