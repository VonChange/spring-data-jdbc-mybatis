package com.vonchange.jdbc.mybatis.repository;

import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;

public interface DataSourceInSql {
    DataSourceWrapper getDataSourceFromSql(String sql);
}
