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

import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.jdbc.abstractjdbc.core.CrudClient;
import com.vonchange.jdbc.abstractjdbc.util.NameQueryUtil;
import com.vonchange.jdbc.mybatis.core.config.ConfigInfo;
import com.vonchange.mybatis.tpl.EntityUtil;
import com.vonchange.mybatis.tpl.model.EntityInfo;

import java.util.Optional;

public class SimpleJdbcRepository<T, ID> implements CrudExtendRepository<T, ID> {

	private final CrudClient crudClient;
	private final ConfigInfo configInfo;

	public SimpleJdbcRepository(CrudClient crudClient, ConfigInfo configInfo) {
		this.crudClient = crudClient;
		this.configInfo = configInfo;
	}


	@Override
	public <S extends T> S save(S entity) {
		EntityInfo entityInfo = EntityUtil.getEntityInfo(entity.getClass());
		Object idValue = BeanUtil.getPropertyT(entity,entityInfo.getIdFieldName());
		if(null==idValue){
			 insert(entity);
			 return entity;
		}
		update(entity);
		return entity;
	}

	@Override
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
		 crudClient.insert(entities);
		 return entities;
	}


	@Override
	@SuppressWarnings("unchecked")
	public Optional<T> findById(ID id) {
		Class<T> tClass= (Class<T>) configInfo.getDomainType();
		return Optional.ofNullable(crudClient.sqlId("findById").param(id).query(tClass).single());
	}

	@Override
	@SuppressWarnings("unchecked")
	public  Iterable<T> findAllById(Iterable<ID> ids) {
		Class<T> tClass= (Class<T>) configInfo.getDomainType();
		return crudClient.sqlId("findByIdIn").param(ids).query(tClass).iterable();
	}

	@Override
	public boolean existsById(ID id) {
		return  crudClient.sqlId(NameQueryUtil.simpleNameSql("existsById",configInfo.getDomainType())).param(id).query(Boolean.class).single();
	}

	@Override
	public Iterable<T> findAll() {
		return null;
	}

	@Override
	public long count() {
		return 0;
	}

	@Override
	public void deleteById(ID id) {
		 crudClient.sqlId("deleteById").param(id).update();
	}

	@Override
	public void delete(T entity) {
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {

	}

	@Override
	public void deleteAll() {

	}


	@Override
	public <S extends T> int insert(S entity) {
		return crudClient.insert(entity);
	}

	@Override
	public <S extends T> int update(S entity) {
		return crudClient.update(entity);
	}
}
