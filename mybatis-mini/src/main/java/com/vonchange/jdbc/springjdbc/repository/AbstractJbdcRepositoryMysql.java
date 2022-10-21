package com.vonchange.jdbc.springjdbc.repository;


import com.vonchange.jdbc.abstractjdbc.core.AbstractJdbcCore;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.dialect.MySQLDialect;

public  abstract   class AbstractJbdcRepositoryMysql extends AbstractJdbcCore {

    @Override
    protected Dialect getDefaultDialect() {
        return new MySQLDialect();
    }

    @Override
    protected DataSourceWrapper getDataSourceFromSql(String sql){
         return null;
    }


    protected boolean  readAllScopeOpen(){
        return false;
    }
    @Override
    protected boolean needReadMdLastModified() {
        return false;
    }
}
