package com.vonchange.jdbc.client;/*
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


import com.vonchange.jdbc.config.ConstantJdbc;
import com.vonchange.jdbc.model.DataSourceWrapper;
import com.vonchange.mybatis.dialect.MySQLDialect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface JdbcClient {
    public static  Map<String, JdbcClient> jdbcClientMap = new ConcurrentHashMap<>();
     static JdbcClient create(DataSourceWrapper dataSourceWrapper) {
         if(null==dataSourceWrapper.getKey()){
             dataSourceWrapper.setKey(ConstantJdbc.DataSourceDefault);
         }
         String key = dataSourceWrapper.getKey();
         if(jdbcClientMap.containsKey(key)){
             return jdbcClientMap.get(key);
         }
         if(null==dataSourceWrapper.getDialect()){
             dataSourceWrapper.setDialect(new MySQLDialect());
         }
         JdbcClient jdbcClient=new DefaultJdbcClient(dataSourceWrapper);
         jdbcClientMap.put(dataSourceWrapper.getKey(),jdbcClient);
         return jdbcClient;
    }
    StatementSpec sql(String sqlId);
    interface StatementSpec {
        StatementSpec param(Object value);

        StatementSpec params(Object... values);

        StatementSpec params(List<?> values);

        StatementSpec param(String name, @Nullable Object value);
        StatementSpec params(Map<String, ?> paramMap);
        <T> MappedQuerySpec<T> query(Class<T> mappedClass);
        int update();
    }


    interface MappedQuerySpec<T> {
        List<T> list();

        Page<T> page(Pageable pageable);

        Iterable<T> iterable();
        T single();
    }

}
