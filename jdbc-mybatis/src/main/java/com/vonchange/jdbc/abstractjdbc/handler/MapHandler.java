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


import com.vonchange.mybatis.tpl.sql.SqlCommentUtil;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * <core>ResultSetHandler</core> implementation that converts the first
 * <core>ResultSet</core> row into a <core>Map</core>. This class is thread
 * safe.
 *
 */
public class MapHandler implements ResultSetExtractor<Map<String,Object>> {

       private  String sql;
       public  MapHandler(String sql){
           this.sql=sql;
       }
    /**
     * Converts the first row in the <core>ResultSet</core> into a
     * <core>Map</core>.
     * @param rs <core>ResultSet</core> to process.
     * @return A <core>Map</core> with the values from the first row or
     * <core>null</core> if there are no rows in the <core>ResultSet</core>.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Map<String,Object> extractData(ResultSet rs) throws SQLException {
        return rs.next() ? this.toMap(rs) : null;
    }
  
    private Map<String,Object> toMap(ResultSet rs) throws SQLException {
       return HandlerUtil.rowToMap(rs, SqlCommentUtil.getLowerNo(sql),SqlCommentUtil.getOrmNo(sql));
    }
}
