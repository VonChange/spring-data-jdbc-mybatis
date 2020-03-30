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
package com.vonchange.spring.data.mybatis.mini.jdbc.repository.support;

import com.vonchange.spring.data.mybatis.mini.jdbc.repository.config.DataSourceWrapperHelper;
import com.vonchange.spring.data.mybatis.mini.jdbc.repository.query.DataSourceKey;
import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import org.springframework.data.mapping.PersistentEntity;

import java.util.List;

/**
 * @author Jens Schauder
 * @author Oliver Gierke
 */

public class SimpleJdbcRepository<T, ID> implements BaseRepository<T,ID> {

	private final  JdbcRepository entityOperations;
	private final  PersistentEntity<T, ?> entity;
	private final DataSourceWrapperHelper dataSourceWrapperHelper;

	public SimpleJdbcRepository(JdbcRepository entityOperations, PersistentEntity<T, ?> entity, DataSourceWrapperHelper dataSourceWrapperHelper) {
		this.entityOperations = entityOperations;
		this.entity = entity;
		this.dataSourceWrapperHelper = dataSourceWrapperHelper;
	}

	private DataSourceWrapper getDataSourceWrapper(){
		DataSourceKey dataSourceKey =  entity.findAnnotation(DataSourceKey.class);
		if(null==dataSourceKey){
			return null;
		}
		String dataSourceKeyValue=dataSourceKey.value();
		return (!"".equals(dataSourceKeyValue))?dataSourceWrapperHelper.getDataSourceWrapperByKey(dataSourceKeyValue):null;
	}
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(S)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <S extends T> ID insert(S instance) {
		return (ID) entityOperations.insert(getDataSourceWrapper(),instance);
	}

	@Override
	public <S extends T> int insertBatch(List<S> entitys) {
		return entityOperations.insertBatch(getDataSourceWrapper(),entitys);
	}
	@Override
	public <S extends T> int  insertBatchDuplicateKey(List<S> entitys){
		return entityOperations.insertBatchDuplicateKey(getDataSourceWrapper(),entitys);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <S extends T> ID insertDuplicateKey(S entity) {
		return (ID) entityOperations.insertDuplicateKey(getDataSourceWrapper(),entity);
	}
	@Override
	public <S extends T> int updateBatch(List<S> entitys) {
		return entityOperations.updateBatch(getDataSourceWrapper(),entitys);
	}

	@Override
	public <S extends T> int updateBatchAllField(List<S> entitys) {
		return entityOperations.updateBatchAllField(getDataSourceWrapper(),entitys);
	}



	@Override
	public <S extends T> int update(S entity) {
		return entityOperations.update(getDataSourceWrapper(),entity);
	}

	@Override
	public <S extends T> int updateAllField(S entity) {
		return entityOperations.updateAllField(getDataSourceWrapper(),entity);
	}


	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)
	 */
	@Override
	public T findById(ID id) {
		return entityOperations.queryById(getDataSourceWrapper(),entity.getType(),id);
	}

}
