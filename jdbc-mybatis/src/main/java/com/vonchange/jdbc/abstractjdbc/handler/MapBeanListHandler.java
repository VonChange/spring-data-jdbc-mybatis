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
import com.vonchange.jdbc.abstractjdbc.config.ConstantJdbc;
import com.vonchange.jdbc.abstractjdbc.util.ConvertMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>ResultSetHandler</code> implementation that converts a
 * <code>ResultSet</code> into a <code>List</code> of beans. This class is
 * thread safe.
 *
 * @param <T> the target processor type
 */
public class MapBeanListHandler<T> implements ResultSetExtractor<Map<String, T>> {
    private static final Logger log = LoggerFactory.getLogger(MapBeanListHandler.class);
    /**
     * The Class of beans produced by this handler.
     */
    private final Class<? extends T> type;
    private final String  keyInMap;

    /**
     * Creates a new instance of BeanListHandler.
     *
     * @param type The Class that objects returned from <code>handle()</code>
     * are created from.
     */


    /**
     * Creates a new instance of BeanListHandler.
     *
     * @param type The Class that objects returned from <code>handle()</code>
     * are created from.
     * to use when converting rows into beans.
     */
    public MapBeanListHandler(Class<? extends T> type,String keyInMap) {
        this.type = type;
        this.keyInMap=keyInMap;
    }

    /**
     * Convert the whole <code>ResultSet</code> into a List of beans with
     * the <code>Class</code> given in the constructor.
     *
     * @param rs The <code>ResultSet</code> to handle.
     *
     * @return A List of beans, never <code>null</code>.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Map<String, T> extractData(ResultSet rs) throws SQLException {
        try {
            return this.toBeanList(rs, type);
        } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            log.error("",e);
        }
        return  new HashMap<>();
    }
    @SuppressWarnings("unchecked")
    private  Map<String, T> toBeanList(ResultSet rs,  Class<? extends T> type) throws SQLException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        Map<String, T> resultMap= new HashMap<>();
        if (!rs.next()) {
            return resultMap;
        }
      T entity ;
        Map<String,Object> newMap;
          do {
              newMap=ConvertMap.newMap(HandlerUtil.rowToMap(rs));
              entity=(T) ConvertMap.convertMap(type,newMap);
              String[] keyInMaps= new String[]{keyInMap};
              if(keyInMap.indexOf(ConstantJdbc.MAPFIELDSPLIT)!=-1){
                  keyInMaps=keyInMap.split(ConstantJdbc.MAPFIELDSPLIT);
              }
              StringBuilder key=new StringBuilder();
              for (String keyIn:keyInMaps) {
                  keyIn=keyIn.toLowerCase();
                  key.append(ConvertUtil.toString(newMap.get(keyIn))).append("_");
              }
        	  resultMap.put(key.substring(0,key.length()-1),entity);
           } while (rs.next());
           return resultMap;
    }
	
}
