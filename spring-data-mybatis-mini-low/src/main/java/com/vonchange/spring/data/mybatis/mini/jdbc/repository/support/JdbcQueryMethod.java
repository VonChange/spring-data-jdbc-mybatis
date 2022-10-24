/*
 * Copyright 2018-2020 the original author or authors.
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


import com.vonchange.spring.data.mybatis.mini.jdbc.repository.query.BatchUpdate;
import com.vonchange.spring.data.mybatis.mini.jdbc.repository.query.ReadDataSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;

import java.lang.reflect.Method;

/**
 * {QueryMethod} implementation that implements a method by executing the query from a { Query} annotation on
 * that method. Binds method arguments to named parameters in the SQL statement.
 *
 * @author Jens Schauder
 * @author Kazuki Shimizu
 */
public class JdbcQueryMethod extends QueryMethod {

	private final Method method;

	public JdbcQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
		super(method, metadata, factory);
		this.method = method;
	}
	public boolean isReadDataSource() {
		return AnnotationUtils.findAnnotation(method, ReadDataSource.class) != null;
	}

	public boolean isBatchUpdate() {
		return AnnotationUtils.findAnnotation(method, BatchUpdate.class) != null;
	}

	public int getBatchSize() {
		return AnnotationUtils.findAnnotation(method, BatchUpdate.class).size();
	}





}
