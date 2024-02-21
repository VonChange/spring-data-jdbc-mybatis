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


import org.springframework.dao.support.DataAccessUtils;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CrudClient {

    StatementSpec sqlId(String sqlId);
     <T> int insert(T entity);
    <T> int insert(List<T> entities);
    <T> int update(T entity);
    /**
     * A statement specification for parameter bindings and query/update execution.
     */
    interface StatementSpec {

        StatementSpec param(@Nullable Object value);
        StatementSpec param(String name, @Nullable Object value);

        StatementSpec params(Map<String, ?> paramMap);

        //ResultQuerySpec query();

        <T> MappedQuerySpec<T> query(Class<T> mappedClass);
        int update();
        <T> int updateBatch(List<T> entities);
    }

    /**
     * A specification for RowMapper-mapped queries.
     *
     * @param <T> the RowMapper-declared result type
     */
    interface MappedQuerySpec<T> {


        /**
         * Retrieve the result as a pre-resolved list of mapped objects,
         * retaining the order from the original database result.
         * @return the result as a detached List, containing mapped objects
         */
        List<T> list();
        Iterable<T> iterable();

        /**
         * Retrieve the result as an order-preserving set of mapped objects.
         * @return the result as a detached Set, containing mapped objects
         * @see #list()
         * @see LinkedHashSet
         */
        default Set<T> set() {
            return new LinkedHashSet<>(list());
        }


//        default Optional<T> optional() {
//            return DataAccessUtils.optionalResult(list());
//        }

        /**
         * Retrieve a single result as a required object instance.
         * @return the single result object (never {@code null})
         * @see #list()
         * @see DataAccessUtils#requiredSingleResult(Collection)
         */
        default T single() {
            return DataAccessUtils.requiredSingleResult(list());
        }
    }

}
