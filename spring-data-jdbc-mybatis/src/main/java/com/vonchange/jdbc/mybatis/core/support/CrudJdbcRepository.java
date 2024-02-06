/*
 * Copyright 2008-2020 the original author or authors.
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

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 *
 * @author Oliver Gierke
 * @author Eberhard Wolff
 */
@NoRepositoryBean
public interface CrudJdbcRepository<T, ID> extends Repository<T, ID> {

	<S extends T> int save(S entity);

	/**
	 * saveAll null will save
	 * @param entities
	 * @param batchSize
	 * @param <S>
	 */
	<S extends T> int saveAll(List<S> entities,int batchSize);

	<S extends T> int saveAllNotNull(List<S> entities,int batchSize);

	/**
	 * update not null fields
	 * @param entity
	 * @param <S>
	 */
	<S extends T> int  update(S entity);

	/**
	 *  update all fields
	 * @param entity
	 * @param <S>
	 */
	<S extends T> int  updateAllField(S entity);

	T findById(ID id);
	List<T> findAllById(List<ID> ids);
	boolean existsById(ID id);
	int deleteById(ID id);
	int deleteAllById(List<? extends ID> ids);

}
