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
package com.vonchange.spring.data.mybatis.mini.jdbc.repository.support;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 *
 * @author Oliver Gierke
 * @author Eberhard Wolff
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends Repository<T, ID> {

	/**
	 * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
	 * entity instance completely.
	 *
	 * @param entity must not be {@literal null}.
	 * @return the saved entity; will never be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
	 */
	<S extends T> int insert(S entity);

	<S extends T> int insertBatch(List<S> entitys,int batchSize);

	<S extends T> int  insertBatchDuplicateKey(List<S> entitys,int batchSize);

	/**
	 * insert into  on duplication key
	 * @param entity
	 * @param <S>
	 */
	<S extends T> int insertDuplicateKey(S entity);

	/**
	 * 默认只更新不为空的字段
	 * @param entity
	 * @param <S>
	 */
	<S extends T> int  update(S entity);

	/**
	 * 更新所有字段
	 * @param entity
	 * @param <S>
	 */
	<S extends T> int  updateAllField(S entity);
	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none found.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	T findById(ID id);

}
