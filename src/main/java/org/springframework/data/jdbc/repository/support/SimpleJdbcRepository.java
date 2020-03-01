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
package org.springframework.data.jdbc.repository.support;

import com.vonchange.jdbc.abstractjdbc.core.JdbcRepostitory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mapping.PersistentEntity;

import java.util.Optional;

/**
 * @author Jens Schauder
 * @author Oliver Gierke
 */
@RequiredArgsConstructor
public class SimpleJdbcRepository<T, ID> implements BaseRepository<T, ID> {

	private final @NonNull JdbcRepostitory entityOperations;
	private final @NonNull PersistentEntity<T, ?> entity;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(S)
	 */
	@Override
	public <S extends T> S save(S instance) {
		return entityOperations.save(instance);
	}

	@Override
	public <S extends T> S saveDuplicateKey(S entity) {
		return entityOperations.saveDuplicateKey(entity);
	}

	@Override
	public <S extends T> int update(S entity) {
		return entityOperations.update(entity);
	}

	@Override
	public <S extends T> int updateAll(S entity) {
		return entityOperations.updateAll(entity);
	}


	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)
	 */
	@Override
	public Optional<T> findById(ID id) {
		return Optional.ofNullable(entityOperations.queryById(entity.getType(),id));
	}

}
