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

import com.vonchange.common.util.ConvertUtil;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <core>ResultSetHandler</core> implementation that converts one
 * <core>ResultSet</core> column into an Object. This class is thread safe.
 *
 */
public class ScalarHandler<T> implements ResultSetExtractor<T> {

    /**
     * The column number to retrieve.
     */
    private final int columnIndex;

    /**
     * The column name to retrieve. Either columnName or columnIndex
     * will be used but never both.
     */
    private final String columnName;

    private Class<T> mappedClass;

    /**
     * Creates a new instance of ScalarHandler. The first column will
     * be returned from <core>handle()</core>.
     */
    public ScalarHandler(Class<T> mappedClass) {
        this(mappedClass,1, null);
    }

    /**
     * Creates a new instance of ScalarHandler.
     *
     * @param columnIndex The index of the column to retrieve from the
     *                    <core>ResultSet</core>.
     */
    public ScalarHandler(Class<T> mappedClass,int columnIndex) {
        this(mappedClass,columnIndex, null);
    }

    /**
     * Creates a new instance of ScalarHandler.
     *
     * @param columnName The name of the column to retrieve from the
     *                   <core>ResultSet</core>.
     */
    public ScalarHandler(Class<T> mappedClass,String columnName) {
        this(mappedClass,1, columnName);
    }

    /**
     * Helper constructor
     * 
     * @param columnIndex The index of the column to retrieve from the
     *                    <core>ResultSet</core>.
     * @param columnName  The name of the column to retrieve from the
     *                    <core>ResultSet</core>.
     */
    private ScalarHandler(Class<T> mappedClass,int columnIndex, String columnName) {
        this.mappedClass=mappedClass;
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }

    /**
     * Returns one <core>ResultSet</core> column as an object via the
     * <core>ResultSet.getObject()</core> method that performs type
     * conversions.
     * 
     * @param rs <core>ResultSet</core> to process.
     * @return The column or <core>null</core> if there are no rows in
     *         the <core>ResultSet</core>.
     *
     * @throws SQLException       if a database access error occurs
     * @throws ClassCastException if the class datatype does not match the column
     *                            type
     *
     */
    // We assume that the user has picked the correct type to match the column
    // so getObject will return the appropriate type and the cast will succeed.
    @Override
    public T extractData(ResultSet rs) throws SQLException {
        if (rs.next()) {
            if (this.columnName == null) {
                return ConvertUtil.toObject(JdbcUtils.getResultSetValue(rs,columnIndex),mappedClass);//rs.getObject(this.columnIndex);
            }
            return ConvertUtil.toObject(rs.getObject(this.columnName),mappedClass);
        }
        return null;
    }
}
