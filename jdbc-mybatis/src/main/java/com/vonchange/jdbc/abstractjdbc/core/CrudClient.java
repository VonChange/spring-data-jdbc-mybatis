package com.vonchange.jdbc.abstractjdbc.core;/*
 * Copyright 2002-2023 the original author or authors.
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


import com.vonchange.jdbc.abstractjdbc.handler.AbstractPageWork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface CrudClient {

    StatementSpec sqlId(String sqlId);
     <T> int insert(T entity);
    <T> int insert(List<T> entities);
    <T> int update(T entity);

    interface StatementSpec {

        StatementSpec param(@Nullable Object value);
        StatementSpec param(String name, @Nullable Object value);
        StatementSpec params(Map<String, ?> paramMap);
        <T> MappedQuerySpec<T> query(Class<T> mappedClass);
        int update();
        <T> int updateBatch(List<T> entities);

        <T> void queryBatch(Class<T> mappedClass, AbstractPageWork<T> pageWork);

        <T> Page<T> queryPage(Class<T> mappedClass, Pageable pageable);
    }


    interface MappedQuerySpec<T> {
        List<T> list();
        Iterable<T> iterable();
        T single();
    }

}
