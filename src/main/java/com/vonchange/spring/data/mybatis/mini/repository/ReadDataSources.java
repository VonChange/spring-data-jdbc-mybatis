package com.vonchange.spring.data.mybatis.mini.repository;

import javax.sql.DataSource;

public interface ReadDataSources {
    DataSource[] allReadDataSources();
}
