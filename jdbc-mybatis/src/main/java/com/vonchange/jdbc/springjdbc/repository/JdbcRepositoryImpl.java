package com.vonchange.jdbc.springjdbc.repository;

import com.vonchange.jdbc.abstractjdbc.core.AbstractJdbcCore;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.dialect.MySQLDialect;

import javax.sql.DataSource;

public class JdbcRepositoryImpl extends AbstractJdbcCore {
    private DataSource dataSource;

    public JdbcRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    protected Dialect getDefaultDialect() {
        return new MySQLDialect();
    }
    @Override
    public DataSourceWrapper getReadDataSource() {
       // return new DataSourceWrapper(dataSource, ConstantJdbc.DataSourceDefault);
        return null;
    }




    @Override
    protected DataSourceWrapper getWriteDataSource() {
        //return new DataSourceWrapper(dataSource, ConstantJdbc.DataSourceDefault);
        return null;
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
