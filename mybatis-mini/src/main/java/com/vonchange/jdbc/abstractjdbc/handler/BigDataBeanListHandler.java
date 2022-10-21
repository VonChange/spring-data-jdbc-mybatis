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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param <T> the target processor type
 */
public class BigDataBeanListHandler<T> implements ResultSetExtractor<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BigDataBeanListHandler.class);
    /**
     * The Class of beans produced by this handler.
     */
    private final Class<? extends T> type;
    private AbstractPageWork abstractPageWork;


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
    public BigDataBeanListHandler(Class<? extends T> type, AbstractPageWork abstractPageWork, String sql) {
        this.type = type;
        this.abstractPageWork = abstractPageWork;
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
    public Integer extractData(ResultSet rs) throws SQLException {
        int pageSize = abstractPageWork.getPageSize();
        try {
              this.toBeanList(rs, type, pageSize);
        } catch (IntrospectionException  | IllegalAccessException | InvocationTargetException e) {
            logger.error("Exception ", e);
        }
        return 1;
    }
    @SuppressWarnings("unchecked")
    private void toBeanList(ResultSet rs, Class<? extends T> type, int pageSize) throws SQLException, IntrospectionException, IllegalAccessException, InvocationTargetException {
        List<T> result = new ArrayList<>();
        Map<String, Object> extData = new HashMap<>();
        if (!rs.next()) {
            abstractPageWork.doPage(result, 0, extData);
            abstractPageWork.setSize(pageSize);
            abstractPageWork.setTotalElements(0L);
            abstractPageWork.setTotalPages(0);
            return ;
        }
        int pageItem = 0;
        int pageNum = 0;
        long count = 0;
        boolean base=false;
        if(ClazzUtils.isBaseType(type)){
            base=true;
        }
        do {
            result.add((T) (base?ConvertUtil.toObject(rs.getObject(1),type):ConvertMap.convertMap(type,ConvertMap.newMap(HandlerUtil.rowToMap(rs)))));
            pageItem++;
            count++;
            if (pageItem == pageSize) {
                abstractPageWork.doPage(result, pageNum, extData);
                pageNum++;
                result = new ArrayList<>();
                pageItem = 0;
            }
        } while (rs.next());
        if (!result.isEmpty()) {
            abstractPageWork.doPage(result, pageNum, extData);
        }
        abstractPageWork.setSize(pageSize);
        abstractPageWork.setTotalElements(count);
        abstractPageWork.setTotalPages(pageNum);
    }

}
