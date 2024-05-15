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
import com.vonchange.common.util.JsonUtil;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.jdbc.client.JdbcClient;
import com.vonchange.jdbc.config.EnumNameQueryType;
import com.vonchange.jdbc.core.CrudClient;
import com.vonchange.jdbc.model.EntityInfo;
import com.vonchange.jdbc.model.SqlParam;
import com.vonchange.jdbc.mybatis.core.config.ConfigInfo;
import com.vonchange.jdbc.util.EntityUtil;
import com.vonchange.jdbc.util.NameQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public class SimpleJdbcRepository<T, ID> implements CrudExtendRepository<T, ID> {
	private static final Logger log = LoggerFactory.getLogger(SimpleJdbcRepository.class);
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
		return Optional.ofNullable(crudClient.jdbc().sql(NameQueryUtil.simpleNameSql("findById",tClass))
				.param(id).query(tClass).single());
	}

	@Override
	@SuppressWarnings("unchecked")
	public  Iterable<T> findAllById(Iterable<ID> ids) {
		if(!ids.iterator().hasNext()){
			return new ArrayList<>();
		}
		Class<T> tClass= (Class<T>) configInfo.getDomainType();
		SqlParam sqlParam= NameQueryUtil.nameSql("findByIdIn",tClass, Collections.singletonList(ids));
		return crudClient.jdbc().sql(sqlParam.getSql()).params(sqlParam.getParams()).query(tClass).iterable();
	}

	@Override
	public boolean existsById(ID id) {
		Assert.notNull(id,"id can not null");
		return  crudClient.jdbc().sql(NameQueryUtil.simpleNameSql("existsById",configInfo.getDomainType())).param(id).query(Boolean.class).single();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterable<T> findAll() {
		Class<T> tClass= (Class<T>) configInfo.getDomainType();
		return  crudClient.jdbc().sql(NameQueryUtil.simpleNameSql("findAll",configInfo.getDomainType()))
				.query(tClass).iterable();
	}

	@Override
	public long count() {
		return crudClient.jdbc().sql(NameQueryUtil.simpleNameSql("countAll",configInfo.getDomainType())).query(Long.class).single();
	}

	@Override
	@Transactional
	public void deleteById(ID id) {
		Assert.notNull(id,"id can not null");
		findById(id).ifPresent(item->{
			log.info("jdbc delete {}", JsonUtil.toJson(item));
			crudClient.jdbc().sql(NameQueryUtil.simpleNameSql("deleteById",configInfo.getDomainType())).param(id)
					.update();
		});
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public void delete(T entity) {
		Assert.notNull(entity,"entity  can not null");
		EntityInfo entityInfo = EntityUtil.getEntityInfo(entity.getClass());
		Object idValue = BeanUtil.getPropertyT(entity,entityInfo.getIdFieldName());
		Assert.notNull(idValue,"entity id value can not null");
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
		for (T t : findAll()) {
			delete(t);
		}
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
	public <S extends T> int insertBatch(List<S> entities, boolean ifNullInsertByFirstEntity) {
		return crudClient.insertBatch(entities,ifNullInsertByFirstEntity);
	}

	@Override
	@Transactional
	public <S extends T> int updateBatch(List<S> entities, boolean ifNullUpdateByFirstEntity) {
		return crudClient.updateBatch(entities,ifNullUpdateByFirstEntity);
	}

	@SuppressWarnings("unchecked")
	private <X>  JdbcClient.MappedQuerySpec<T> findByExample(X example){
		Class<T> tClass= (Class<T>) configInfo.getDomainType();
		SqlParam sqlParam= NameQueryUtil.exampleSql(EnumNameQueryType.Find,tClass,example);
		return crudClient.jdbc().sql(sqlParam.getSql()).params(sqlParam.getParams())
				.query(tClass);
	}
	@Override
	public <X> List<T> findAll(X example) {
		return  findByExample(example).list();
	}
	@Override
	public <X> Optional<T> findOne(X example) {
		return  Optional.ofNullable(findByExample(example).single());
	}
	@Override
	public < X> Page<T> findAll(X example, Pageable pageable) {
		return  findByExample(example).page(pageable);
	}
	@Override
	public <X> Long count(X example){
		Class<?> tClass= configInfo.getDomainType();
		SqlParam sqlParam= NameQueryUtil.exampleSql(EnumNameQueryType.Count,tClass,example);
		return crudClient.jdbc().sql(sqlParam.getSql()).params(sqlParam.getParams())
				.query(Long.class).single();
	}

	@Override
	public void deleteAllById(Iterable<? extends ID> ids) {
		for (ID id : ids) {
			deleteById(id);
		}
	}
}
