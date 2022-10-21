package com.vonchange.jdbc.springjdbc.repository;

import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;

import javax.sql.DataSource;


public    class JdbcRepositoryBaseImpl extends AbstractJbdcRepositoryMysql implements JdbcRepository {
    private DataSource dataSource;
    public JdbcRepositoryBaseImpl(DataSource dataSource){
        this.dataSource=dataSource;
    }
    @Override
    public DataSourceWrapper getReadDataSource() {
        return new DataSourceWrapper(dataSource,"dataSource");
    }

    @Override
    protected DataSourceWrapper getWriteDataSource() {
        return new DataSourceWrapper(dataSource,"dataSource");
    }

    @Override
    protected int batchSize() {
        return 5000;
    }

    @Override
    protected boolean logRead() {
        return false;
    }

    @Override
    protected boolean logWrite() {
        return false;
    }

    @Override
    protected boolean logFullSql() {
        return false;
    }




}
