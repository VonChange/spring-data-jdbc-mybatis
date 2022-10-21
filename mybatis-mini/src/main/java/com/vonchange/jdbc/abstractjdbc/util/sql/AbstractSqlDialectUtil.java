package com.vonchange.jdbc.abstractjdbc.util.sql;

import com.vonchange.jdbc.abstractjdbc.config.ConstantJdbc;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.dialect.MySQLDialect;
import com.vonchange.mybatis.dialect.OracleDialect;


/**
 * Created by 冯昌义 on 2018/4/17.
 */
public abstract class AbstractSqlDialectUtil {
    protected abstract Dialect getDefaultDialect();
    protected abstract String  getDataSource();
    public   Dialect getDialect(String sql){
        if(sql.contains("@mysql")){
            return new MySQLDialect();
        }
        if(sql.contains("@oracle")){
            return new OracleDialect();
        }
        return   getDefaultDialect();
    }
    public   String getDataSource(String sql){
        if(sql.contains(ConstantJdbc.DataSource.FLAG)){
            int start=sql.indexOf(ConstantJdbc.DataSource.FLAG);
            int end = sql.indexOf(' ',start+ConstantJdbc.DataSource.FLAG.length());
            return  sql.substring(start+ConstantJdbc.DataSource.FLAG.length(),end);
        }
        if(null==getDataSource()){
            return  ConstantJdbc.DataSource.DEFAULT;
        }
        return  getDataSource();
    }
}
