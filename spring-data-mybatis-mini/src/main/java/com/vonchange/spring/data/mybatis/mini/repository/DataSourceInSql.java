package com.vonchange.spring.data.mybatis.mini.repository;

import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;

public interface DataSourceInSql {
    DataSourceWrapper getDataSourceFromSql(String sql);
}
