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
package org.springframework.data.mybatis.mini.jdbc.repository.support;

import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mybatis.mini.jdbc.repository.config.DataSourceWrapperHelper;
import org.springframework.data.mybatis.mini.jdbc.repository.query.DataSourceKey;

import java.util.List;

/**
 * @author Jens Schauder
 * @author Oliver Gierke
 */
@RequiredArgsConstructor
public class SimpleJdbcRepository<T, ID> implements BaseRepository<T,ID> {

	private final @NonNull JdbcRepository entityOperations;
	private final @NonNull PersistentEntity<T, ?> entity;
	private final @NonNull DataSourceWrapperHelper dataSourceWrapperHelper;



	private DataSourceWrapper getDataSourceWrapper(){
		DataSourceKey dataSourceKey =  entity.findAnnotation(DataSourceKey.class);
		if(null==dataSourceKey){
			return null;
		}
		String dataSourceKeyValue=dataSourceKey.value();
		return null!=dataSourceKeyValue?dataSourceWrapperHelper.getDataSourceWrapperByKey(dataSourceKeyValue):null;
	}
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(S)
	 */
	@Override
	public <S extends T> ID insert(S instance) {
		return (ID) entityOperations.insert(getDataSourceWrapper(),instance);
	}

	@Override
	public <S extends T> int insertBatch(List<S> entitys) {
		return entityOperations.insertBatch(getDataSourceWrapper(),entitys);
	}
	@Override
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
