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

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 *
 * @author Oliver Gierke
 * @author Eberhard Wolff
 */
@NoRepositoryBean
public interface CrudExtendRepository<T, ID> extends CrudRepository<T, ID> {
	/**
	 * insert not null filed
	 * @param entity
	 * @return
	 * @param <S>
	 */

	<S extends T> int insert(S entity);

	/**
	 * update not null field
	 * @param entity
	 * @param <S>
	 */
	<S extends T> int  update(S entity);

	<S extends T> int insert(List<S> entities, boolean ifNullInsertByFirstEntity);
	<S extends T> int update(List<S> entities,boolean isNullUpdateByFirstEntity);

}
