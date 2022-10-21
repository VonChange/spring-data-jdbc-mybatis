/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vonchange.jdbc.abstractjdbc.handler;

import com.vonchange.jdbc.abstractjdbc.util.ConvertMap;
import com.vonchange.mybatis.common.util.ConvertUtil;
import com.vonchange.mybatis.tpl.clazz.ClazzUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <core>ResultSetHandler</core> implementation that converts a
 * <core>ResultSet</core> into a <core>List</core> of beans. This class is
 * thread safe.
 *
 * @param <T> the target processor type
 */
public class BeanListHandler<T> implements ResultSetExtractor<List<T>> {

    private static final Logger log = LoggerFactory.getLogger(BeanListHandler.class);
    /**
     * The Class of beans produced by this handler.
     */
    private final Class<? extends T> type;


    /**
     * Creates a new instance of BeanListHandler.
     *
     * @param type The Class that objects returned from <core>handle()</core>
     * are created from.
     */


    /**
     * Creates a new instance of BeanListHandler.
     *
     * @param type The Class that objects returned from <core>handle()</core>
     *             are created from.
     *             to use when converting rows into beans.
     */
    public BeanListHandler(Class<? extends T> type) {
        this.type = type;
    }

    /**
     * Convert the whole <core>ResultSet</core> into a List of beans with
     * the <core>Class</core> given in the constructor.
     *
     * @param rs The <core>ResultSet</core> to handle.
     * @return A List of beans, never <core>null</core>.
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<T> extractData(ResultSet rs) throws SQLException {
        try {
            return this.toBeanList(rs, type);
        } catch (IntrospectionException  | IllegalAccessException | InvocationTargetException e) {
            log.error("Exception ", e);
        }
        return new ArrayList<>();
    }

    private List<T> toBeanList(ResultSet rs, Class<? extends T> type) throws SQLException, IntrospectionException, IllegalAccessException, InvocationTargetException {
        List<T> results = new ArrayList<>();
        if (!rs.next()) {
            return results;
        }
        boolean base=false;
        if(ClazzUtils.isBaseType(type)){
            base=true;
        }
        T entity;
        do {
            entity= (T) (base?ConvertUtil.toObject(rs.getObject(1),type):ConvertMap.convertMap(type,ConvertMap.newMap(HandlerUtil.rowToMap(rs))));
            results.add(entity);
        } while (rs.next());
        return results;
    }

}
