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

import com.vonchange.jdbc.mybatis.core.config.ConfigInfo;
import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.List;

public class SimpleJdbcRepository<T, ID> implements CrudJdbcRepository<T, ID> {

	private final JdbcRepository entityOperations;
	private final ConfigInfo configInfo;

	public SimpleJdbcRepository(@Qualifier("jdbcRepository") JdbcRepository entityOperations, ConfigInfo configInfo) {
		this.entityOperations = entityOperations;
		this.configInfo = configInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#save(S)
	 */
	@Override
	public <S extends T> int save(S instance) {
		return entityOperations.insert(configInfo.getDataSourceWrapper(), instance);
	}

	@Override
	public <S extends T> int saveAll(List<S> entities, int batchSize) {
		return entityOperations.insertBatch(configInfo.getDataSourceWrapper(), entities, true,batchSize);
	}
	@Override
	public <S extends T> int saveAllNotNull(List<S> entities, int batchSize) {
		return entityOperations.insertBatch(configInfo.getDataSourceWrapper(), entities,false, batchSize);
	}

	@Override
	public <S extends T> int update(S entity) {
		return entityOperations.update(configInfo.getDataSourceWrapper(), entity);
	}

	@Override
	public <S extends T> int updateAllField(S entity) {
		return entityOperations.updateAllField(configInfo.getDataSourceWrapper(), entity);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T findById(ID id) {
		return (T) entityOperations.queryById(configInfo.getDataSourceWrapper(), configInfo.getDomainType(), id);
	}

	@Override
	public  List<T> findAllById(List<ID> ids) {
		return (List<T>) entityOperations.queryByIds(configInfo.getDataSourceWrapper(),configInfo.getDomainType(), ids);
	}

	@Override
	public boolean existsById(ID id) {
		return  entityOperations.existsById(configInfo.getDataSourceWrapper(), configInfo.getDomainType(), id);
	}

	@Override
	public int deleteById(ID id) {
		return entityOperations.deleteById(configInfo.getDataSourceWrapper(),  configInfo.getDomainType(), id);
	}

	@Override
	public int deleteAllById(List<? extends ID> ids) {
		return entityOperations.deleteByIds(configInfo.getDataSourceWrapper(), configInfo.getDomainType(), ids);
	}

}
