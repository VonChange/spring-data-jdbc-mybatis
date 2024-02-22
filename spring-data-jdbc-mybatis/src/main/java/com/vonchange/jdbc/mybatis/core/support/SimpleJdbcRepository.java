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

import com.vonchange.common.util.Assert;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.jdbc.abstractjdbc.core.CrudClient;
import com.vonchange.jdbc.abstractjdbc.util.NameQueryUtil;
import com.vonchange.jdbc.mybatis.core.config.ConfigInfo;
import com.vonchange.mybatis.tpl.EntityUtil;
import com.vonchange.mybatis.tpl.model.EntityInfo;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public class SimpleJdbcRepository<T, ID> implements CrudExtendRepository<T, ID> {

	private final CrudClient crudClient;
	private final ConfigInfo configInfo;

	public SimpleJdbcRepository(CrudClient crudClient, ConfigInfo configInfo) {
		this.crudClient = crudClient;
		this.configInfo = configInfo;
	}


	@Override
	@Transactional
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

	/**
	 *  user inset update instead
	 * @param entities must not be {@literal null}.
	 * @return
	 * @param <S>
	 */
	@Override
	@Transactional
	@Deprecated
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
		return Streamable.of(entities).stream()
				.map(this::save)
				.collect(Collectors.toList());
	}


	@Override
	@SuppressWarnings("unchecked")
	public Optional<T> findById(ID id) {
		Assert.notNull(id,"id can not null");
		Class<T> tClass= (Class<T>) configInfo.getDomainType();
		return Optional.ofNullable(crudClient.sqlId("findById").param(id).query(tClass).single());
	}

	@Override
	@SuppressWarnings("unchecked")
	public  Iterable<T> findAllById(Iterable<ID> ids) {
		if(!ids.iterator().hasNext()){
			return new ArrayList<>();
		}
		Class<T> tClass= (Class<T>) configInfo.getDomainType();
		return crudClient.sqlId("findByIdIn").param(ids).query(tClass).iterable();
	}

	@Override
	public boolean existsById(ID id) {
		Assert.notNull(id,"id can not null");
		return  crudClient.sqlId(NameQueryUtil.simpleNameSql("existsById",configInfo.getDomainType())).param(id).query(Boolean.class).single();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterable<T> findAll() {
		Class<T> tClass= (Class<T>) configInfo.getDomainType();
		return  crudClient.sqlId(NameQueryUtil.simpleNameSql("findAll",configInfo.getDomainType()))
				.query(tClass).iterable();
	}

	@Override
	public long count() {
		return crudClient.sqlId(NameQueryUtil.simpleNameSql("countAll",configInfo.getDomainType())).query(Long.class).single();
	}

	@Override
	@Transactional
	public void deleteById(ID id) {
		Assert.notNull(id,"id can not null");
		 crudClient.sqlId(NameQueryUtil.simpleNameSql("deleteById",configInfo.getDomainType())).param(id).update();
	}

	@Override
	@Transactional
	public void delete(T entity) {
		EntityInfo entityInfo = EntityUtil.getEntityInfo(entity.getClass());
		Object idValue = BeanUtil.getPropertyT(entity,entityInfo.getIdFieldName());
		deleteById((ID) idValue);
	}

	@Override
	@Transactional
	public void deleteAll(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	@Override
	@Transactional
	public void deleteAll() {
		crudClient.sqlId(NameQueryUtil.simpleNameSql("deleteAll",configInfo.getDomainType())).update();
	}


	@Override
	@Transactional
	public <S extends T> int insert(S entity) {
		return crudClient.insert(entity);
	}

	@Override
	@Transactional
	public <S extends T> int update(S entity) {
		return crudClient.update(entity);
	}

	@Override
	@Transactional
	public <S extends T> int insert(List<S> entities, boolean ifNullInsertByFirstEntity) {
		return crudClient.insert(entities,ifNullInsertByFirstEntity);
	}

	@Override
	@Transactional
	public <S extends T> int update(List<S> entities, boolean isNullUpdateByFirstEntity) {
		return crudClient.update(entities,isNullUpdateByFirstEntity);
	}
}
