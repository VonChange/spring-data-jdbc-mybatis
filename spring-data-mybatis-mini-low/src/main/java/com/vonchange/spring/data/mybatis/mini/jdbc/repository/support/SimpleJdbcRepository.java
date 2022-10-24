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

import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import com.vonchange.spring.data.mybatis.mini.jdbc.repository.config.ConfigInfo;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jens Schauder
 * @author Oliver Gierke
 */

public class SimpleJdbcRepository<T, ID extends Serializable> implements BaseRepository<T,ID> {

	private final  JdbcRepository entityOperations;
	private final ConfigInfo configInfo;

	public SimpleJdbcRepository(@Qualifier("jdbcRepository")JdbcRepository entityOperations, ConfigInfo configInfo) {
		this.entityOperations = entityOperations;
		this.configInfo = configInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(S)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <S extends T> int insert(S instance) {
		return  entityOperations.insert(configInfo.getDataSourceWrapper(),instance);
	}

	@Override
	public <S extends T> int insertBatch(List<S> entitys,int batchSize) {
		return entityOperations.insertBatch(configInfo.getDataSourceWrapper(),entitys,batchSize);
	}
	@Override
	public <S extends T> int  insertBatchDuplicateKey(List<S> entitys,int batchSize){
		return entityOperations.insertBatchDuplicateKey(configInfo.getDataSourceWrapper(),entitys,batchSize);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <S extends T> int insertDuplicateKey(S entity) {
		return entityOperations.insertDuplicateKey(configInfo.getDataSourceWrapper(),entity);
	}


	@Override
	public <S extends T> int update(S entity) {
		return entityOperations.update(configInfo.getDataSourceWrapper(),entity);
	}

	@Override
	public <S extends T> int updateAllField(S entity) {
		return entityOperations.updateAllField(configInfo.getDataSourceWrapper(),entity);
	}


	@Override
	@SuppressWarnings("unchecked")
	public T findById(ID id) {
		return (T) entityOperations.queryById(configInfo.getDataSourceWrapper(),configInfo.getType(),id);
	}

}
